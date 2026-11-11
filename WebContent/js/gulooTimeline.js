/*
 * 咕溜時光軸(電腦版)：由 GulooTimelineComposer 呼叫 hmsGuloo.render(rootUuid, data, opts) 繪製。
 * 篩選、搜尋、快速輸入的解析都在前端處理；新增/刪除透過 zAu.send 送回 composer。
 *
 * data = {
 *   entities: [{uid, alias, typeIdx, birth:'yyyy-MM-dd'|''}],
 *   cates:    [{uid, name, color}],
 *   stamps:   [{uid, date:'yyyy-MM-dd'|'', desp, remark, entities:[uid], cates:[uid]}]
 * }
 * opts = {newUid, clearQuick, closeDrawer}
 */
window.hmsGuloo = window.hmsGuloo || (function () {
	var HUMAN = 1; // EntityType.HUMAN
	var WEEKDAY = '日一二三四五六';
	var PALETTE = ['#d9714e', '#4f8fc0', '#5ba37a', '#c9a227', '#9b6bc2', '#d0607f', '#3f9e9c', '#8a7a66'];
	// 標籤可選的顏色(另可自訂)
	var TAG_SWATCHES = ['#d9714e', '#e8a33d', '#c9a227', '#5ba37a', '#3f9e9c', '#4f8fc0', '#6c7fd1', '#9b6bc2', '#d0607f', '#8a7a66'];
	var DEFAULT_TAG_COLOR = '#b8ad9e';

	var root = null, uuid = null;
	var data = { entities: [], cates: [], stamps: [] };
	var entityMap = {}, cateMap = {};
	var state = { members: {}, tags: {}, keyword: '', selUid: null, form: null, fv: {}, formErr: '' };
	var suggest = { list: [], idx: 0, token: null };

	// -------------------------------------------------------------------------------
	// utils
	function esc(s) {
		return String(s == null ? '' : s).replace(/[&<>"']/g, function (c) {
			return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
		});
	}
	function $(sel) { return root.querySelector(sel); }
	function pad(n) { return (n < 10 ? '0' : '') + n; }
	function ymd(d) { return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()); }
	function parseYmd(s) {
		var m = /^(\d{4})-(\d{1,2})-(\d{1,2})$/.exec(s || '');
		if (!m) return null;
		var d = new Date(+m[1], +m[2] - 1, +m[3]);
		return d.getMonth() === +m[2] - 1 ? d : null; // 擋掉 2/30 這類日期
	}
	function today() { var d = new Date(); return new Date(d.getFullYear(), d.getMonth(), d.getDate()); }
	/** 標籤顏色：沒選(或不是有效顏色)就是灰色；手機版舊資料可能是不含 # 的 hex，一併接受 */
	function tagColor(cate) {
		var c = String(cate.color || '').trim();
		if (/^[0-9a-f]{3}([0-9a-f]{3})?$/i.test(c)) c = '#' + c;
		return c && window.CSS && CSS.supports && CSS.supports('color', c) ? c : DEFAULT_TAG_COLOR;
	}
	function entityColor(e) { return PALETTE[data.entities.indexOf(e) % PALETTE.length]; }
	function highlight(text) {
		var s = esc(text);
		if (!state.keyword) return s;
		var kw = esc(state.keyword).replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
		return s.replace(new RegExp(kw, 'gi'), function (m) { return '<span class="gl-hl">' + m + '</span>'; });
	}

	/** 某成員在某天的年紀：未滿 1 歲顯示月數，未滿 6 歲顯示歲+月 */
	function ageAt(e, date) {
		if (!e || e.typeIdx !== HUMAN || !date) return '';
		var b = parseYmd(e.birth);
		if (!b || date < b) return '';
		var months = (date.getFullYear() - b.getFullYear()) * 12 + date.getMonth() - b.getMonth();
		if (date.getDate() < b.getDate()) months--;
		var y = Math.floor(months / 12), m = months % 12;
		if (y === 0) return m === 0 ? '未滿月' : m + '個月';
		if (y < 6 && m > 0) return y + '歲' + m + '個月';
		return y + '歲';
	}

	function avatar(e, small) {
		return '<span class="gl-avatar' + (small ? ' gl-sm' : '') + '" style="--c:' + entityColor(e) + '">'
			+ esc((e.alias || '?').charAt(0)) + '</span>';
	}
	function tagPill(c, extra) {
		return '<span class="gl-tag' + (extra || '') + '"><span class="gl-dot" style="--c:' + tagColor(c) + '"></span>'
			+ esc(c.name) + '</span>';
	}

	// -------------------------------------------------------------------------------
	// skeleton (只建一次)
	function mount() {
		root.innerHTML =
			'<div class="gl-app">'
			+ '<div class="gl-header">'
			+ '  <div class="gl-title-row"><h1 class="gl-title">咕溜</h1><span class="gl-subtitle">我們家的時光軸</span>'
			+ '    <div class="gl-stats" id="glStats"></div></div>'
			+ '  <div class="gl-quick">'
			+ '    <div class="gl-quick-box"><span class="gl-quick-icon">✦</span>'
			+ '      <input class="gl-quick-input" id="glQuick" autocomplete="off" spellcheck="false"'
			+ '        placeholder="記下一件事…  例：2026-11-11 第一次自己騎腳踏車 @成員 #標籤 // 備註" />'
			+ '      <button class="gl-quick-btn" id="glQuickBtn" disabled>新增</button></div>'
			+ '    <div class="gl-quick-preview" id="glPreview"></div>'
			+ '    <div class="gl-suggest" id="glSuggest" style="display:none"></div>'
			+ '  </div>'
			+ '</div>'
			+ '<div class="gl-body">'
			+ '  <aside class="gl-side" id="glSide"></aside>'
			+ '  <main class="gl-main" id="glMain"><div class="gl-main-inner" id="glTimeline"></div></main>'
			+ '</div>'
			+ '<div class="gl-drawer" id="glDrawer"></div>'
			+ '<div class="gl-picker" id="glPicker" style="display:none"></div>'
			+ '</div>';
		bind();
		renderPreview();
	}

	// -------------------------------------------------------------------------------
	// filter
	function matches(s) {
		var mk = Object.keys(state.members), tk = Object.keys(state.tags);
		if (mk.length && !s.entities.some(function (u) { return state.members[u]; })) return false;
		if (tk.length && !s.cates.some(function (u) { return state.tags[u]; })) return false;
		if (state.keyword) {
			var kw = state.keyword.toLowerCase();
			if ((s.desp || '').toLowerCase().indexOf(kw) < 0 && (s.remark || '').toLowerCase().indexOf(kw) < 0) return false;
		}
		return true;
	}
	function sortedStamps() {
		return data.stamps.slice().sort(function (a, b) {
			if (!a.date !== !b.date) return a.date ? -1 : 1; // 未指定日期放最後
			return a.date < b.date ? 1 : a.date > b.date ? -1 : 0;
		});
	}

	// -------------------------------------------------------------------------------
	// render: stats / side / timeline
	function renderStats() {
		var y = String(new Date().getFullYear());
		var thisYear = data.stamps.filter(function (s) { return s.date.indexOf(y) === 0; }).length;
		var latest = sortedStamps()[0], ago = '';
		if (latest && latest.date) {
			var days = Math.round((today() - parseYmd(latest.date)) / 86400000);
			ago = days <= 0 ? '今天' : days + ' 天前';
		}
		$('#glStats').innerHTML = '<span>共<b>' + data.stamps.length + '</b>個咕溜</span>'
			+ '<span>今年<b>' + thisYear + '</b>個</span>'
			+ (ago ? '<span>最近一次 <b style="font-size:14px">' + ago + '</b></span>' : '');
	}

	function renderSide() {
		var cntE = {}, cntC = {}, years = {};
		data.stamps.forEach(function (s) {
			s.entities.forEach(function (u) { cntE[u] = (cntE[u] || 0) + 1; });
			s.cates.forEach(function (u) { cntC[u] = (cntC[u] || 0) + 1; });
			var y = s.date ? s.date.substr(0, 4) : '未指定';
			years[y] = (years[y] || 0) + 1;
		});
		var anyFilter = Object.keys(state.members).length || Object.keys(state.tags).length || state.keyword;
		var h = '<input class="gl-search" id="glSearch" placeholder="搜尋簡述、備註…" value="' + esc(state.keyword) + '" />';
		if (anyFilter) h += '<button class="gl-link" data-act="clear">清除篩選</button>';

		/* 成員 */
		h += '<div class="gl-side-title"><span>成員</span><button data-act="form" data-form="entity" title="新增成員">＋ 新增</button></div>';
		if (state.form === 'entity') h += entityForm();
		if (!data.entities.length) h += '<div class="gl-note">尚未建立成員</div>';
		data.entities.forEach(function (e) {
			h += '<button class="gl-facet' + (state.members[e.uid] ? ' gl-on' : '') + '" data-act="member" data-uid="' + esc(e.uid) + '">'
				+ avatar(e, true) + '<span>' + esc(e.alias) + '</span>'
				+ '<span class="gl-facet-count">' + (cntE[e.uid] || 0) + '</span>'
				+ '<span class="gl-facet-del" data-act="delEntity" data-uid="' + esc(e.uid) + '" title="刪除成員">×</span></button>';
		});

		/* 標籤 */
		h += '<div class="gl-side-title"><span>標籤</span><button data-act="form" data-form="cate" title="新增標籤">＋ 新增</button></div>';
		if (state.form === 'cate') h += cateForm();
		if (!data.cates.length) h += '<div class="gl-note">尚未建立標籤</div>';
		data.cates.forEach(function (c) {
			h += '<button class="gl-facet' + (state.tags[c.uid] ? ' gl-on' : '') + '" data-act="tag" data-uid="' + esc(c.uid) + '">'
				+ '<span class="gl-dot gl-dot-pick" style="--c:' + tagColor(c) + '" data-act="color" data-uid="' + esc(c.uid) + '" title="選擇顏色"></span>'
				+ '<span>' + esc(c.name) + '</span>'
				+ '<span class="gl-facet-count">' + (cntC[c.uid] || 0) + '</span>'
				+ '<span class="gl-facet-del" data-act="delCate" data-uid="' + esc(c.uid) + '" title="刪除標籤">×</span></button>';
		});

		h += '<div class="gl-side-title"><span>年份</span></div>';
		Object.keys(years).sort().reverse().forEach(function (y) {
			h += '<button class="gl-facet" data-act="year" data-year="' + esc(y) + '"><span>' + esc(y) + '</span>'
				+ '<span class="gl-facet-count">' + years[y] + '</span></button>';
		});

		var side = $('#glSide');
		var focused = document.activeElement && document.activeElement.id === 'glSearch';
		side.innerHTML = h;
		if (focused) { var inp = $('#glSearch'); inp.focus(); inp.setSelectionRange(inp.value.length, inp.value.length); }
	}

	// -------------------------------------------------------------------------------
	// 新增成員／標籤的表單(值存在 state.fv，側欄重繪時不會消失)
	var ENTITY_TYPES = [[1, '人'], [3, '動產'], [2, '不動產'], [4, '物品']]; // EntityType idx

	function entityForm() {
		var fv = state.fv;
		return '<div class="gl-form">'
			+ '<input id="glFvAlias" data-fv="alias" placeholder="名稱(不可含空白)" value="' + esc(fv.alias || '') + '" />'
			+ '<div class="gl-form-row"><select id="glFvType" data-fv="typeIdx">'
			+ ENTITY_TYPES.map(function (t) { return '<option value="' + t[0] + '"' + (+(fv.typeIdx || 1) === t[0] ? ' selected' : '') + '>' + t[1] + '</option>'; }).join('')
			+ '</select><input type="date" data-fv="birth" title="生日(可不填)" value="' + esc(fv.birth || '') + '" /></div>'
			+ formErr()
			+ '<div class="gl-form-row gl-form-btns"><button class="gl-btn" data-act="closeForm">取消</button>'
			+ '<button class="gl-btn gl-btn-primary" data-act="addEntity">新增成員</button></div></div>';
	}

	function cateForm() {
		var fv = state.fv, cur = fv.color || '';
		return '<div class="gl-form">'
			+ '<input id="glFvName" data-fv="name" placeholder="名稱(不可含空白)" value="' + esc(fv.name || '') + '" />'
			+ '<div class="gl-swatches">'
			+ TAG_SWATCHES.map(function (s) {
				return '<span class="gl-swatch' + (s === cur ? ' gl-on' : '') + '" style="--c:' + s + '" data-act="formColor" data-color="' + s + '"></span>';
			}).join('')
			+ '<span class="gl-swatch gl-swatch-none' + (!cur ? ' gl-on' : '') + '" data-act="formColor" data-color="" title="不指定(灰色)"></span></div>'
			+ formErr()
			+ '<div class="gl-form-row gl-form-btns"><button class="gl-btn" data-act="closeForm">取消</button>'
			+ '<button class="gl-btn gl-btn-primary" data-act="addCate">新增標籤</button></div></div>';
	}

	function formErr() {
		return state.formErr ? '<div class="gl-form-err">' + esc(state.formErr) + '</div>' : '';
	}

	function openForm(which) {
		state.form = state.form === which ? null : which;
		state.fv = {};
		state.formErr = '';
		renderSide();
		var first = root.querySelector('.gl-form input');
		if (first) first.focus();
	}

	function submitForm() {
		var fv = state.fv, name;
		if (state.form === 'entity') {
			name = String(fv.alias || '').trim();
			if (!checkName(name, data.entities, 'alias', '成員')) return;
			send('onAddEntity', { alias: name, typeIdx: +(fv.typeIdx || 1), birth: fv.birth || '' });
		} else if (state.form === 'cate') {
			name = String(fv.name || '').trim();
			if (!checkName(name, data.cates, 'name', '標籤')) return;
			send('onAddCate', { name: name, color: fv.color || '' });
		}
	}

	/** 快速輸入用 @名稱、#名稱 比對，所以名稱不可含空白、不可重複 */
	function checkName(name, list, key, label) {
		var msg = !name ? '請輸入' + label + '名稱。'
			: /\s/.test(name) ? label + '名稱不可包含空白(快速輸入以空白分隔)。'
			: findByName(list, key, name) ? '已經有名為「' + name + '」的' + label + '。' : '';
		state.formErr = msg;
		if (msg) {
			renderSide();
			var inp = root.querySelector('.gl-form input'); // 重繪後游標回到名稱欄，方便直接修改
			if (inp) { inp.focus(); inp.select(); }
			return false;
		}
		return true;
	}

	function renderFlash() {
		var t = today(), md = ymd(t).substr(5), y = t.getFullYear();
		var list = data.stamps.filter(function (s) { return s.date && s.date.substr(5) === md && +s.date.substr(0, 4) < y; });
		if (!list.length) return '';
		var h = '<div class="gl-flash"><div class="gl-flash-title">那年今日</div>';
		list.forEach(function (s) {
			h += '<div class="gl-flash-item" data-act="open" data-uid="' + esc(s.uid) + '"><span class="gl-flash-ago">'
				+ (y - s.date.substr(0, 4)) + ' 年前</span>' + esc(s.desp) + '</div>';
		});
		return h + '</div>';
	}

	function renderTimeline(newUid) {
		var list = sortedStamps().filter(matches);
		var h = renderFlash();
		if (!list.length) {
			h += '<div class="gl-empty"><div class="gl-empty-big">🌱</div>'
				+ (data.stamps.length ? '沒有符合條件的咕溜' : '還沒有任何咕溜，從上方記下第一件事吧') + '</div>';
			$('#glTimeline').innerHTML = h;
			return;
		}
		var yearCount = {};
		list.forEach(function (s) { var y = s.date ? s.date.substr(0, 4) : '未指定'; yearCount[y] = (yearCount[y] || 0) + 1; });

		var curY = null, curM = null;
		list.forEach(function (s) {
			var d = parseYmd(s.date);
			var y = d ? String(d.getFullYear()) : '未指定', m = d ? d.getMonth() + 1 : null;
			if (y !== curY) {
				curY = y; curM = null;
				h += '<div class="gl-year" data-year="' + esc(y) + '"><span class="gl-year-num">' + esc(y) + '</span>'
					+ '<span class="gl-year-count">' + yearCount[y] + ' 個咕溜</span></div>';
			}
			if (m !== curM && m) { curM = m; h += '<div class="gl-month">' + m + ' 月</div>'; }

			var cates = s.cates.map(function (u) { return cateMap[u]; }).filter(Boolean);
			var ents = s.entities.map(function (u) { return entityMap[u]; }).filter(Boolean);
			var meta = cates.map(function (c) { return tagPill(c); }).join('');
			meta += ents.map(function (e) {
				var age = ageAt(e, d);
				return '<span class="gl-who">' + avatar(e, true) + esc(e.alias)
					+ (age ? '<span class="gl-who-age">· ' + age + '</span>' : '') + '</span>';
			}).join('');

			h += '<div class="gl-item">'
				+ '<div class="gl-item-date">' + (d ? '<div class="gl-item-day">' + d.getDate() + '</div><div class="gl-item-wd">週' + WEEKDAY.charAt(d.getDay()) + '</div>' : '') + '</div>'
				+ '<div class="gl-rail"><span class="gl-rail-dot" style="--c:' + (cates[0] ? tagColor(cates[0]) : 'var(--gl-accent)') + '"></span></div>'
				+ '<div class="gl-card' + (s.uid === state.selUid ? ' gl-sel' : '') + (s.uid === newUid ? ' gl-new' : '')
				+ '" data-act="open" data-uid="' + esc(s.uid) + '">'
				+ '<div class="gl-card-desp">' + highlight(s.desp) + '</div>'
				+ (s.remark ? '<div class="gl-card-remark">' + highlight(s.remark) + '</div>' : '')
				+ (meta ? '<div class="gl-card-meta">' + meta + '</div>' : '')
				+ '</div></div>';
		});
		$('#glTimeline').innerHTML = h;

		if (newUid) {
			var card = root.querySelector('.gl-card[data-uid="' + newUid + '"]');
			if (card) card.scrollIntoView({ block: 'center' });
		}
	}

	// -------------------------------------------------------------------------------
	// drawer
	function openDrawer(uid) {
		var s = data.stamps.filter(function (x) { return x.uid === uid; })[0];
		if (!s) return closeDrawer();
		state.selUid = uid;
		var d = parseYmd(s.date);
		var ents = s.entities.map(function (u) { return entityMap[u]; }).filter(Boolean);
		var cates = s.cates.map(function (u) { return cateMap[u]; }).filter(Boolean);
		$('#glDrawer').innerHTML =
			'<div class="gl-drawer-head"><h3>咕溜內容</h3><button class="gl-x" data-act="close" title="關閉 (Esc)">×</button></div>'
			+ '<div class="gl-drawer-body">'
			+ '<div class="gl-field"><label>日期</label><input type="date" value="' + esc(s.date) + '" disabled /></div>'
			+ '<div class="gl-field"><label>簡述</label><input value="' + esc(s.desp) + '" disabled /></div>'
			+ '<div class="gl-field"><label>成員</label><div class="gl-card-meta">'
			+ ents.map(function (e) {
				var age = ageAt(e, d);
				return '<span class="gl-who">' + avatar(e, true) + esc(e.alias) + (age ? '<span class="gl-who-age">· ' + age + '</span>' : '') + '</span>';
			}).join('') + '</div></div>'
			+ '<div class="gl-field"><label>標籤</label><div class="gl-card-meta">'
			+ (cates.length ? cates.map(function (c) { return tagPill(c); }).join('') : '<span class="gl-who-age">(無)</span>') + '</div></div>'
			+ '<div class="gl-field"><label>備註</label><textarea rows="4" disabled>' + esc(s.remark) + '</textarea></div>'
			+ '<div class="gl-note">Prototype：目前 MembershipService 沒有修改咕溜的方法，所以欄位先唯讀；要開放修改需在後端補上 update。</div>'
			+ '</div>'
			+ '<div class="gl-drawer-foot"><button class="gl-btn gl-btn-danger" data-act="delete" data-uid="' + esc(s.uid) + '">刪除</button>'
			+ '<button class="gl-btn" data-act="close">關閉</button></div>';
		$('#glDrawer').classList.add('gl-open');
		root.querySelectorAll('.gl-card.gl-sel').forEach(function (c) { c.classList.remove('gl-sel'); });
		var card = root.querySelector('.gl-card[data-uid="' + uid + '"]');
		if (card) card.classList.add('gl-sel');
	}
	function closeDrawer() {
		state.selUid = null;
		if (!root) return;
		$('#glDrawer').classList.remove('gl-open');
		root.querySelectorAll('.gl-card.gl-sel').forEach(function (c) { c.classList.remove('gl-sel'); });
	}

	// -------------------------------------------------------------------------------
	// 標籤顏色選擇
	function openPicker(uid, anchor) {
		var c = cateMap[uid];
		if (!c) return;
		var cur = String(c.color || '').trim().toLowerCase();
		if (/^[0-9a-f]{6}$/.test(cur)) cur = '#' + cur;
		var custom = /^#[0-9a-f]{6}$/.test(cur) ? cur : '#d9714e';
		var p = $('#glPicker');
		p.innerHTML = '<div class="gl-picker-title">「' + esc(c.name) + '」的顏色</div>'
			+ '<div class="gl-swatches">'
			+ TAG_SWATCHES.map(function (s) {
				return '<span class="gl-swatch' + (s === cur ? ' gl-on' : '') + '" style="--c:' + s + '" data-act="setcolor" data-uid="' + esc(uid) + '" data-color="' + s + '" title="' + s + '"></span>';
			}).join('')
			+ '<span class="gl-swatch gl-swatch-none' + (!cur ? ' gl-on' : '') + '" data-act="setcolor" data-uid="' + esc(uid) + '" data-color="" title="不指定(灰色)"></span>'
			+ '</div>'
			+ '<div class="gl-picker-custom"><span>自訂</span><input type="color" id="glPickerInput" value="' + custom + '" />'
			+ '<button class="gl-btn" data-act="setcolor" data-uid="' + esc(uid) + '" data-custom="1">套用</button></div>';
		var rr = root.getBoundingClientRect(), ar = anchor.getBoundingClientRect();
		p.style.left = (ar.left - rr.left) + 'px';
		p.style.top = (ar.bottom - rr.top + 6) + 'px';
		p.style.display = 'block';
	}
	function closePicker() { if (root) $('#glPicker').style.display = 'none'; }

	// -------------------------------------------------------------------------------
	// quick add：日期 + 簡述 + @成員 + #標籤 + // 備註
	function findByName(list, key, name) {
		var n = name.toLowerCase();
		return list.filter(function (x) { return String(x[key]).toLowerCase() === n; })[0];
	}
	function parseDate(tok) {
		var t = today(), m;
		if (tok === '今天') return t;
		if (tok === '昨天') return new Date(t.getFullYear(), t.getMonth(), t.getDate() - 1);
		if (tok === '前天') return new Date(t.getFullYear(), t.getMonth(), t.getDate() - 2);
		if ((m = /^(\d{4})[-/.](\d{1,2})[-/.](\d{1,2})$/.exec(tok))) return parseYmd(m[1] + '-' + m[2] + '-' + m[3]);
		if ((m = /^(\d{1,2})[/.](\d{1,2})$/.exec(tok))) return parseYmd(t.getFullYear() + '-' + m[1] + '-' + m[2]);
		return null;
	}
	function parseQuick(text) {
		var r = { date: null, desp: '', remark: '', entities: [], cates: [], unknown: [] };
		var i = text.indexOf('//');
		if (i >= 0) { r.remark = text.substr(i + 2).trim(); text = text.substr(0, i); }
		var words = [];
		text.split(/\s+/).forEach(function (tok) {
			if (!tok) return;
			var x;
			if (tok.charAt(0) === '@' && tok.length > 1) {
				x = findByName(data.entities, 'alias', tok.substr(1));
				if (x) { if (r.entities.indexOf(x) < 0) r.entities.push(x); } else r.unknown.push(tok);
			} else if (tok.charAt(0) === '#' && tok.length > 1) {
				x = findByName(data.cates, 'name', tok.substr(1));
				if (x) { if (r.cates.indexOf(x) < 0) r.cates.push(x); } else r.unknown.push(tok);
			} else if (!r.date && (x = parseDate(tok))) {
				r.date = x;
			} else words.push(tok);
		});
		r.desp = words.join(' ');
		r.dateDefault = !r.date;
		if (!r.date) r.date = today();
		r.problems = [];
		if (!r.desp) r.problems.push('請輸入簡述');
		if (!r.entities.length) r.problems.push('至少需要一位成員(@)');
		if (r.unknown.length) r.problems.push('找不到：' + r.unknown.join('、'));
		return r;
	}

	function renderPreview() {
		var input = $('#glQuick'), text = input.value.trim(), btn = $('#glQuickBtn'), pv = $('#glPreview');
		if (!text) {
			pv.innerHTML = '用 <span class="gl-kbd">@</span> 標記成員、<span class="gl-kbd">#</span> 加標籤、<span class="gl-kbd">//</span> 之後是備註，'
				+ '日期可寫 2026-11-11、11/11、昨天(省略為今天)。按 <span class="gl-kbd">Enter</span> 新增，<span class="gl-kbd">/</span> 隨時回到這裡。';
			btn.disabled = true;
			return;
		}
		var r = parseQuick(text), d = r.date;
		var h = '<span class="gl-tag" style="--c:#8a7a66">📅 ' + ymd(d) + ' 週' + WEEKDAY.charAt(d.getDay()) + (r.dateDefault ? '(今天)' : '') + '</span>';
		r.entities.forEach(function (e) { var a = ageAt(e, d); h += '<span class="gl-who">' + avatar(e, true) + esc(e.alias) + (a ? '<span class="gl-who-age">· ' + a + '</span>' : '') + '</span>'; });
		r.cates.forEach(function (c) { h += tagPill(c); });
		if (r.desp) h += '<span>「' + esc(r.desp) + '」</span>';
		if (r.remark) h += '<span>備註：' + esc(r.remark) + '</span>';
		if (r.problems.length) h += '<span class="gl-warn">' + esc(r.problems.join('；')) + '</span>';
		pv.innerHTML = h;
		btn.disabled = r.problems.length > 0;
	}

	function submitQuick() {
		var text = $('#glQuick').value.trim();
		if (!text) return;
		var r = parseQuick(text);
		if (r.problems.length) return renderPreview();
		send('onQuickAdd', {
			date: ymd(r.date), desp: r.desp, remark: r.remark,
			entityUids: r.entities.map(function (e) { return e.uid; }),
			cateUids: r.cates.map(function (c) { return c.uid; })
		});
		$('#glQuickBtn').disabled = true;
	}

	/** 游標所在的 @xxx / #xxx 字詞，供自動完成 */
	function caretToken(input) {
		var pos = input.selectionStart, v = input.value;
		var start = v.lastIndexOf(' ', pos - 1) + 1;
		var tok = v.substring(start, pos);
		if (!/^[@#]/.test(tok)) return null;
		return { start: start, end: pos, kind: tok.charAt(0), text: tok.substr(1).toLowerCase() };
	}
	function updateSuggest() {
		var input = $('#glQuick'), box = $('#glSuggest'), tk = caretToken(input);
		if (!tk) { box.style.display = 'none'; suggest.list = []; return; }
		var src = tk.kind === '@' ? data.entities : data.cates, key = tk.kind === '@' ? 'alias' : 'name';
		suggest.list = src.filter(function (x) { return String(x[key]).toLowerCase().indexOf(tk.text) >= 0; });
		suggest.token = tk;
		suggest.idx = Math.min(suggest.idx, Math.max(0, suggest.list.length - 1));
		if (!suggest.list.length) { box.style.display = 'none'; return; }
		box.innerHTML = suggest.list.map(function (x, i) {
			return '<div class="gl-suggest-item' + (i === suggest.idx ? ' gl-on' : '') + '" data-act="pick" data-idx="' + i + '">'
				+ (tk.kind === '@' ? avatar(x, true) : '<span class="gl-dot" style="--c:' + tagColor(x) + '"></span>')
				+ esc(x[key]) + '</div>';
		}).join('');
		box.style.left = Math.min(input.offsetLeft + 20, input.offsetWidth - 180) + 'px';
		box.style.display = 'block';
	}
	function pickSuggest(i) {
		var x = suggest.list[i], tk = suggest.token, input = $('#glQuick');
		if (!x || !tk) return;
		var name = tk.kind === '@' ? x.alias : x.name;
		var v = input.value;
		input.value = v.substr(0, tk.start) + tk.kind + name + ' ' + v.substr(tk.end).replace(/^\s+/, '');
		var caret = tk.start + name.length + 2;
		input.setSelectionRange(caret, caret);
		input.focus();
		suggest.list = [];
		$('#glSuggest').style.display = 'none';
		renderPreview();
	}

	// -------------------------------------------------------------------------------
	function send(evt, payload) {
		var w = zk.Widget.$(uuid);
		if (w) zAu.send(new zk.Event(w, evt, payload, { toServer: true }));
	}

	function bind() {
		root.addEventListener('click', function (ev) {
			var t = ev.target.closest('[data-act]');
			if (!ev.target.closest('#glPicker') && !(t && t.getAttribute('data-act') === 'color')) closePicker();
			if (!t || !root.contains(t)) return;
			var act = t.getAttribute('data-act'), uid = t.getAttribute('data-uid');
			if (act === 'form') {
				openForm(t.getAttribute('data-form'));
			} else if (act === 'closeForm') {
				openForm(null);
			} else if (act === 'formColor') {
				state.fv.color = t.getAttribute('data-color');
				renderSide();
			} else if (act === 'addEntity' || act === 'addCate') {
				submitForm();
			} else if (act === 'delEntity') {
				send('onDeleteEntity', { uid: uid });
			} else if (act === 'delCate') {
				send('onDeleteCate', { uid: uid });
			} else if (act === 'color') {
				openPicker(uid, t);
			} else if (act === 'setcolor') {
				var color = t.getAttribute('data-custom') ? $('#glPickerInput').value : t.getAttribute('data-color');
				send('onCateColor', { uid: uid, color: color });
				closePicker();
			} else if (act === 'member' || act === 'tag') {
				var set = act === 'member' ? state.members : state.tags;
				if (set[uid]) delete set[uid]; else set[uid] = true;
				renderSide(); renderTimeline();
			} else if (act === 'clear') {
				state.members = {}; state.tags = {}; state.keyword = '';
				renderSide(); renderTimeline();
			} else if (act === 'year') {
				var y = root.querySelector('.gl-year[data-year="' + t.getAttribute('data-year') + '"]');
				if (y) $('#glMain').scrollTop = y.offsetTop - $('#glMain').offsetTop;
			} else if (act === 'open') {
				openDrawer(uid);
			} else if (act === 'close') {
				closeDrawer();
			} else if (act === 'delete') {
				send('onDeleteStamp', { uid: uid });
			} else if (act === 'pick') {
				pickSuggest(+t.getAttribute('data-idx'));
			}
		});
		root.addEventListener('input', function (ev) {
			var fvKey = ev.target.getAttribute && ev.target.getAttribute('data-fv');
			if (fvKey) {
				state.fv[fvKey] = ev.target.value;
				// 開始修改就收起上一次的錯誤訊息
				if (state.formErr) { state.formErr = ''; var err = root.querySelector('.gl-form-err'); if (err) err.remove(); }
			}
			if (ev.target.id === 'glSearch') { state.keyword = ev.target.value.trim(); renderTimeline(); }
			if (ev.target.id === 'glQuick') { suggest.idx = 0; renderPreview(); updateSuggest(); }
		});
		root.addEventListener('keydown', function (ev) {
			if (ev.target.getAttribute && ev.target.getAttribute('data-fv') && ev.key === 'Enter') { submitForm(); ev.preventDefault(); return; }
			if (ev.target.id !== 'glQuick') return;
			var open = suggest.list.length && $('#glSuggest').style.display !== 'none';
			if (open && (ev.key === 'ArrowDown' || ev.key === 'ArrowUp')) {
				suggest.idx = (suggest.idx + (ev.key === 'ArrowDown' ? 1 : -1) + suggest.list.length) % suggest.list.length;
				updateSuggest(); ev.preventDefault();
			} else if (open && (ev.key === 'Enter' || ev.key === 'Tab')) {
				pickSuggest(suggest.idx); ev.preventDefault();
			} else if (ev.key === 'Enter') {
				submitQuick(); ev.preventDefault();
			} else if (ev.key === 'Escape') {
				$('#glSuggest').style.display = 'none';
			}
		});
		root.addEventListener('focusout', function (ev) {
			if (ev.target.id === 'glQuick') setTimeout(function () { if (root) $('#glSuggest').style.display = 'none'; }, 150);
		});
		$('#glQuickBtn').addEventListener('click', submitQuick);
	}

	// 全域快捷鍵：/ 聚焦快速輸入、Esc 關閉側邊面板(頁面切走後 root 不在 DOM 就忽略)
	document.addEventListener('keydown', function (ev) {
		if (!root || !document.body.contains(root)) return;
		var tag = (ev.target.tagName || '').toLowerCase();
		if (ev.key === 'Escape') { closePicker(); closeDrawer(); }
		else if (ev.key === '/' && tag !== 'input' && tag !== 'textarea') { $('#glQuick').focus(); ev.preventDefault(); }
	});

	// -------------------------------------------------------------------------------
	function render(rootUuid, d, opts) {
		opts = opts || {};
		var el = document.getElementById(rootUuid);
		if (!el) return;
		if (el !== root || !el.querySelector('.gl-app')) { root = el; uuid = rootUuid; mount(); }
		data = d;
		entityMap = {}; cateMap = {};
		data.entities.forEach(function (e) { entityMap[e.uid] = e; });
		data.cates.forEach(function (c) { cateMap[c.uid] = c; });
		// 已不存在的篩選條件拿掉
		Object.keys(state.members).forEach(function (u) { if (!entityMap[u]) delete state.members[u]; });
		Object.keys(state.tags).forEach(function (u) { if (!cateMap[u]) delete state.tags[u]; });

		if (opts.clearQuick) { $('#glQuick').value = ''; }
		if (opts.closeForm) { state.form = null; state.fv = {}; state.formErr = ''; }
		renderPreview();
		renderStats();
		renderSide();
		renderTimeline(opts.newUid);
		if (opts.closeDrawer) closeDrawer();
		else if (state.selUid) openDrawer(state.selUid);
	}

	return { render: render };
})();
