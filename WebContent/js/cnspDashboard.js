/* 消費統計 dashboard：由 CnspDashboardComposer 呼叫 hmsCnspDashboard.render(data) 繪製圖表 */
window.hmsCnspDashboard = window.hmsCnspDashboard || (function () {
	var COLOR = {
		cnsp: '#2a78d6',    // 消費
		pay: '#eb6834',     // 付款
		payed: '#1baf7a',   // 已付
		payable: '#eda100', // 未付
		ink: '#52514e',
		grid: '#e1e0d9'
	};
	var charts = {};
	var data = null;

	function fmt(n) {
		return Number(n).toLocaleString('zh-TW');
	}

	// 共用設定：細線格線、千分位、tooltip 顯示金額
	function baseOptions(horizontal) {
		var valueAxis = {
			grid: { color: COLOR.grid },
			border: { display: false },
			ticks: { color: COLOR.ink, callback: function (v) { return fmt(v); } }
		};
		var catAxis = { grid: { display: false }, ticks: { color: COLOR.ink } };
		return {
			responsive: true,
			maintainAspectRatio: false,
			indexAxis: horizontal ? 'y' : 'x',
			interaction: { mode: 'index', intersect: false },
			scales: horizontal ? { x: valueAxis, y: catAxis } : { x: catAxis, y: valueAxis },
			plugins: {
				legend: { labels: { color: COLOR.ink, boxWidth: 12 } },
				tooltip: {
					callbacks: {
						label: function (ctx) {
							return (ctx.dataset.label ? ctx.dataset.label + '：' : '') + fmt(ctx.parsed[horizontal ? 'x' : 'y']);
						}
					}
				}
			}
		};
	}

	function bar(label, values, color, extra) {
		var ds = {
			label: label, data: values, backgroundColor: color,
			borderRadius: 4, borderSkipped: 'start', maxBarThickness: 28
		};
		for (var k in extra) ds[k] = extra[k];
		return ds;
	}

	function draw(id, config) {
		if (charts[id])
			charts[id].destroy();
		var canvas = document.getElementById(id);
		if (!canvas)
			return;
		charts[id] = new Chart(canvas, config);
	}

	function drawTrend() {
		draw('dsbTrend', {
			type: 'bar',
			data: {
				labels: data.months,
				datasets: [bar('消費', data.monthCnsp, COLOR.cnsp), bar('付款', data.monthPay, COLOR.pay)]
			},
			options: baseOptions(false)
		});
	}

	// 類型目錄；cate 有值時改顯示該目錄下的類型明細
	function drawCategory(cate) {
		var items = cate ? cate.types : data.categories;
		var options = baseOptions(true);
		options.plugins.legend.display = false;
		options.onHover = function (evt, els) {
			evt.native.target.style.cursor = !cate && els.length ? 'pointer' : 'default';
		};
		options.onClick = function (evt, els) {
			if (!cate && els.length)
				drawCategory(data.categories[els[0].index]);
		};
		draw('dsbCategory', {
			type: 'bar',
			data: {
				labels: items.map(function (i) { return i.name; }),
				datasets: [bar('消費', items.map(function (i) { return i.amount; }), COLOR.cnsp)]
			},
			options: options
		});
		document.getElementById('dsbCategoryTitle').textContent = cate ? '消費類型：' + cate.name : '消費類型目錄';
		document.getElementById('dsbCategoryBack').style.display = cate ? '' : 'none';
	}

	function drawPaymentType() {
		var options = baseOptions(true);
		options.scales.x.stacked = true;
		options.scales.y.stacked = true;
		var pts = data.paymentTypes;
		draw('dsbPaymentType', {
			type: 'bar',
			data: {
				labels: pts.map(function (p) { return p.name; }),
				datasets: [
					bar('已付', pts.map(function (p) { return p.payed; }), COLOR.payed, { borderSkipped: false, borderRadius: 0 }),
					bar('未付', pts.map(function (p) { return p.payable; }), COLOR.payable, { borderSkipped: false, borderRadius: 0 })
				]
			},
			options: options
		});
	}

	function render(d) {
		data = d;
		drawTrend();
		drawCategory(null);
		drawPaymentType();
		var back = document.getElementById('dsbCategoryBack');
		if (back)
			back.onclick = function () { drawCategory(null); };
	}

	return { render: render };
})();
