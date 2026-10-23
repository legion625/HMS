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
	// 跨年比較：由新到舊依序取色(固定順序，不循環)
	var YEAR_COLORS = ['#2a78d6', '#eb6834', '#1baf7a', '#eda100', '#e87ba4'];
	var charts = {};
	var data = null;
	var yoyMetric = 'cnsp';

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
				legend: { labels: { color: COLOR.ink, boxWidth: 10, boxHeight: 10, padding: 8 } },
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

	// 月平均參考線(虛線，每個月同一個值)
	function avgLine(label, avg, color) {
		return {
			type: 'line', label: label, data: data.months.map(function () { return avg; }),
			borderColor: color, borderWidth: 2, borderDash: [6, 4],
			pointRadius: 0, pointHoverRadius: 0, fill: false
		};
	}

	function drawTrend() {
		draw('dsbTrend', {
			type: 'bar',
			data: {
				labels: data.months,
				datasets: [
					bar('消費', data.monthCnsp, COLOR.cnsp), bar('付款', data.monthPay, COLOR.pay),
					avgLine('消費月平均', data.cnspAvg, COLOR.cnsp), avgLine('付款月平均', data.payAvg, COLOR.pay)
				]
			},
			options: baseOptions(false)
		});
	}

	// 跨年同月比較：x 軸 1~12 月，每個年度一條線；期間外的月份不畫點
	function drawYoy() {
		var values = yoyMetric === 'pay' ? data.monthPay : data.monthCnsp;
		var byYear = {};
		data.months.forEach(function (ym, i) {
			var y = ym.substring(0, 4), m = parseInt(ym.substring(5, 7), 10) - 1;
			if (!byYear[y])
				byYear[y] = [null, null, null, null, null, null, null, null, null, null, null, null];
			byYear[y][m] = values[i];
		});
		var years = Object.keys(byYear).sort().reverse().slice(0, YEAR_COLORS.length);
		var datasets = years.map(function (y, i) {
			return {
				label: y + '年', data: byYear[y],
				borderColor: YEAR_COLORS[i], backgroundColor: YEAR_COLORS[i],
				borderWidth: 2, pointRadius: 4, pointHoverRadius: 6,
				pointBorderColor: '#fcfcfb', pointBorderWidth: 2,
				spanGaps: false, tension: 0
			};
		}).reverse(); // 舊年度在前，圖例由舊到新
		var labels = [];
		for (var m = 1; m <= 12; m++)
			labels.push(m + '月');
		draw('dsbYoy', { type: 'line', data: { labels: labels, datasets: datasets }, options: baseOptions(false) });
	}

	function bindYoyToggle() {
		var btns = document.querySelectorAll('.dsb-toggle-btn');
		Array.prototype.forEach.call(btns, function (btn) {
			btn.onclick = function () {
				yoyMetric = btn.getAttribute('data-metric');
				Array.prototype.forEach.call(btns, function (b) {
					b.classList.toggle('dsb-active', b === btn);
				});
				drawYoy();
			};
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
		drawYoy();
		bindYoyToggle();
		drawCategory(null);
		drawPaymentType();
		var back = document.getElementById('dsbCategoryBack');
		if (back)
			back.onclick = function () { drawCategory(null); };
	}

	return { render: render };
})();
