var app = angular.module('sentinelDashboardApp');

app.controller('MetricCtl', ['$scope', '$stateParams', 'MetricService', '$interval', '$timeout',
	function ($scope, $stateParams, MetricService, $interval, $timeout) {
		moment.locale('zh-cn');

		$scope.loading = false;
		$scope.display = false;
		$scope.charts = [];
		$scope.startTime = new Date();
		$scope.endTime = new Date();
		$scope.startTime.setMinutes($scope.endTime.getMinutes() - 5);
		$scope.startTimeFmt = formatDate($scope.startTime);
		$scope.endTimeFmt = formatDate($scope.endTime);
		$scope.endDateBeforeRender = endDateBeforeRender;
		$scope.endDateOnSetTime = endDateOnSetTime;
		$scope.startDateBeforeRender = startDateBeforeRender;
		$scope.startDateOnSetTime = startDateOnSetTime;

		function formatDate(date) {
			return moment(date).format('YYYY/MM/DD HH:mm:ss');
		}

		function startDateOnSetTime (newDate, oldDate) {
			$scope.startTime = new Date(formatDate(newDate));
			$scope.$broadcast('start-date-changed');
		}

		function endDateOnSetTime (newDate, oldDate) {
			$scope.endTime = new Date(formatDate(newDate));
			$scope.$broadcast('end-date-changed');
		}

		function startDateBeforeRender ($dates) {
			if ($scope.dateRangeEnd) {
				var activeDate = moment($scope.dateRangeEnd);

				$dates.filter(function (date) {
					return date.localDateValue() >= activeDate.valueOf()
				}).forEach(function (date) {
					date.selectable = false;
				})
			}
		}

		function endDateBeforeRender ($view, $dates) {
			if ($scope.dateRangeStart) {
				var activeDate = moment($scope.dateRangeStart).subtract(1, $view).add(1, 'minute');

				$dates.filter(function (date) {
					return date.localDateValue() <= activeDate.valueOf()
				}).forEach(function (date) {
					date.selectable = false;
				})
			}
		}

		$scope.app = $stateParams.app;

		// 数据自动刷新频率
		$scope.autoRefresh=true;
		$scope.refreshInterval = 1000 * 15;
		$scope.refreshItems = [
			{val : 15, text : "15秒刷新"},
			{val : 30, text : "30秒刷新"},
			{val : 60, text : "60秒刷新"},
			{val : 0, text : "自定义时间"}
		];
		$scope.onRefreshIntervalChange = (item) => {
			if (item.val === 0) {
				$scope.autoRefresh = false;
				$interval.cancel(intervalId);
				$scope.display = true;
			} else {
				$scope.autoRefresh = true;
				$scope.refreshInterval = 1000 * item.val;
				$scope.display = false;
			}
			reInitIdentityDatas();
		};

		$scope.servicePageConfig = {
			pageSize: 5,
			currentPageIndex: 1,
			totalPage: 1,
			totalCount: 0,
		};

		$scope.servicesChartConfigs = [];

		$scope.pageChanged = function (newPageNumber) {
			$scope.servicePageConfig.currentPageIndex = newPageNumber;
			reInitIdentityDatas();
		};

		let searchT;
		$scope.searchService = function () {
			$timeout.cancel(searchT);
			searchT = $timeout(function () {
				reInitIdentityDatas();
			}, 600);
		}

		var intervalId;
		reInitIdentityDatas();

		function reInitIdentityDatas() {
			$interval.cancel(intervalId);
			if ($scope.autoRefresh) {
				// 快速触发，将自定义时间调整为近 5 分钟
				$scope.startTime = new Date();
				$scope.endTime = new Date();
				$scope.startTime.setMinutes($scope.endTime.getMinutes() - 5);
				$scope.dateRangeStart = moment($scope.startTime).format('YYYY-MM-DD HH:mm:ss');
				$scope.dateRangeEnd = moment($scope.endTime).format('YYYY-MM-DD HH:mm:ss');

				intervalId = $interval(function () {
					$scope.startTime = new Date();
					$scope.endTime = new Date();
					$scope.startTime.setMinutes($scope.endTime.getMinutes() - 5);
					$scope.dateRangeStart = moment($scope.startTime).format('YYYY-MM-DD HH:mm:ss');
					$scope.dateRangeEnd = moment($scope.endTime).format('YYYY-MM-DD HH:mm:ss');
					queryIdentityDatas();
				}, $scope.refreshInterval);
			}
			queryIdentityDatas();
		};

		$scope.$on('$destroy', function () {
			$interval.cancel(intervalId);
		});

		$scope.initAllChart = function () {
			//revoke useless charts positively
			while($scope.charts.length > 0) {
				let chart = $scope.charts.pop();
				chart.destroy();
			}
			$.each($scope.metrics, function (idx, metric) {
				if (idx == $scope.metrics.length - 1) {
					return;
				}
				const chart = new G2.Chart({
					container: 'chart' + idx,
					forceFit: true,
					width: 100,
					height: 250,
					padding: [10, 30, 70, 50]
				});
				$scope.charts.push(chart);
				var maxQps = 0;
				for (var i in metric.data) {
					var item = metric.data[i];
					if (item.passQps > maxQps) {
						maxQps = item.passQps;
					}
					if (item.blockQps > maxQps) {
						maxQps = item.blockQps;
					}
					/*if (item.rt > maxQps) {
						maxQps = item.rt;
					}*/
				}
				chart.source(metric.data);
				chart.scale('timestamp', {
					type: 'time',
					mask: 'YYYY-MM-DD HH:mm:ss'
				});
				chart.scale('passQps', {
					min: 0,
					max: maxQps,
					fine: true,
					alias: '通过 QPS'
				});
				chart.scale('blockQps', {
					min: 0,
					max: maxQps,
					fine: true,
					alias: '拒绝 QPS',
				});
				chart.scale('rt', {
					min: 0,
					fine: true,
					alias: '响应时间（ms）',
				});
				chart.axis('rt', {
					grid: null,
					label: null
				});
				chart.axis('blockQps', {
					grid: null,
					label: null
				});

				chart.axis('timestamp', {
					label: {
						textStyle: {
							textAlign: 'center', // 文本对齐方向，可取值为： start center end
							fill: '#404040', // 文本的颜色
							fontSize: '11', // 文本大小
							textBaseline: 'top', // 文本基准线，可取 top middle bottom，默认为middle
						},
						autoRotate: true,
						formatter: function (text, item, index) {
							return text.substring(5, 11 + 5);
						}
					}
				});
				chart.legend({
					custom: true,
					position: 'bottom',
					allowAllCanceled: true,
					itemFormatter: function (val) {
						if ('passQps' === val) {
							return '通过 QPS';
						}
						if ('blockQps' === val) {
							return '拒绝 QPS';
						}
						if ('rt' === val) {
							return '响应时间（ms）';
						}
						return val;
					},
					items: [
						{ value: 'passQps', marker: { symbol: 'hyphen', stroke: 'green', radius: 5, lineWidth: 2 } },
						{ value: 'blockQps', marker: { symbol: 'hyphen', stroke: 'red', radius: 5, lineWidth: 2 } },
						{ value: 'rt', marker: {symbol: 'hyphen', stroke: 'gray', radius: 5, lineWidth: 2} }
					],
					onClick: function (ev) {
						const item = ev.item;
						const value = item.value;
						const checked = ev.checked;
						const geoms = chart.getAllGeoms();
						for (var i = 0; i < geoms.length; i++) {
							const geom = geoms[i];
							if (geom.getYScale().field === value) {
								if (checked) {
									geom.show();
								} else {
									geom.hide();
								}
							}
						}
					}
				});
				chart.line().position('timestamp*passQps').size(1).color('green').shape('smooth');
				chart.line().position('timestamp*blockQps').size(1).color('red').shape('smooth');
				chart.line().position('timestamp*rt').size(0.5).color('gray').shape('smooth');
				G2.track(false);
				chart.render();
			});
		};

		$scope.metrics = [];
		$scope.emptyObjs = [];
		function queryIdentityDatas() {
			$scope.loading = true;
			var params = {
				app: $scope.app,
				pageIndex: $scope.servicePageConfig.currentPageIndex,
				pageSize: $scope.servicePageConfig.pageSize,
				desc: $scope.isDescOrder,
				searchKey: $scope.serviceQuery,
				startTime: $scope.startTime.getTime(),
				endTime: $scope.endTime.getTime()
			};
			MetricService.queryAppSortedIdentities(params).success(function (data) {
				$scope.metrics = [];
				$scope.emptyObjs = [];
				if (data.code === 0 && data.data) {
					var metricsObj = data.data.metric;
					var identityNames = Object.keys(metricsObj);
					if (identityNames.length < 1) {
						$scope.emptyServices = true;
					} else {
						$scope.emptyServices = false;
					}
					$scope.servicePageConfig.totalPage = data.data.totalPage;
					$scope.servicePageConfig.pageSize = data.data.pageSize;
					var totalCount = data.data.totalCount;
					$scope.servicePageConfig.totalCount = totalCount;
					for (i = 0; i < totalCount; i++) {
						$scope.emptyObjs.push({});
					}
					$.each(identityNames, function (idx, identityName) {
						var identityDatas = metricsObj[identityName];
						var metrics = {};
						metrics.resource = identityName;
						// metrics.data = identityDatas;
						metrics.data = fillZeros(identityDatas);
						metrics.shortData = lastOfArray(identityDatas, 6);
						$scope.metrics.push(metrics);
					});
					// push an empty element in the last, for ng-init reasons.
					$scope.metrics.push([]);
				} else {
					$scope.emptyServices = true;
					console.log(data.msg);
				}
				$scope.loading = false;
			});
		};
		function fillZeros(metricData) {
			if (!metricData || metricData.length == 0) {
				return [];
			}
			var filledData = [];
			filledData.push(metricData[0]);
			var lastTime = metricData[0].timestamp / 1000;
			for (var i = 1; i < metricData.length; i++) {
				var curTime = metricData[i].timestamp / 1000;
				if (curTime > lastTime + 1) {
					for (var j = lastTime + 1; j < curTime; j++) {
						filledData.push({
							"timestamp": j * 1000,
							"passQps": 0,
							"blockQps": 0,
							"successQps": 0,
							"exception": 0,
							"rt": 0,
							"count": 0
						})
					}
				}
				filledData.push(metricData[i]);
				lastTime = curTime;
			}
			return filledData;
		}
		function lastOfArray(arr, n) {
			if (!arr.length) {
				return [];
			}
			var rs = [];
			for (i = 0; i < n && i < arr.length; i++) {
				rs.push(arr[arr.length - 1 - i]);
			}
			return rs;
		}

		$scope.isDescOrder = true;
		$scope.setDescOrder = function () {
			$scope.isDescOrder = true;
			reInitIdentityDatas();
		}
		$scope.setAscOrder = function () {
			$scope.isDescOrder = false;
			reInitIdentityDatas();
		}
	}]);
