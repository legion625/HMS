/**
 * 滑動卡片(.swipe-container)的行為。
 * 以事件委派掛在 document 上，只綁定一次；之後動態新增的卡片不需要重新綁定。
 */
(function () {
  if (window.__hmsSwipeInited) return; // 防止重複載入時重複綁定
  window.__hmsSwipeInited = true;

  const THRESHOLD = 50; // 水平滑動超過此距離(px)才算數
  let target = null, startX = 0, startY = 0, endX = 0, endY = 0;

  function closeAll(except) {
    document.querySelectorAll('.swipe-container.swiped').forEach(function (el) {
      if (el !== except) el.classList.remove('swiped');
    });
  }

  document.addEventListener('touchstart', function (e) {
    target = e.target.closest('.swipe-container');
    if (!target) return;
    // 每次觸控都重設座標，避免沿用上一次滑動的值
    startX = endX = e.touches[0].clientX;
    startY = endY = e.touches[0].clientY;
  }, { passive: true });

  document.addEventListener('touchmove', function (e) {
    if (!target) return;
    endX = e.touches[0].clientX;
    endY = e.touches[0].clientY;
  }, { passive: true });

  document.addEventListener('touchend', function () {
    if (!target) return;
    const el = target;
    target = null;

    const deltaX = endX - startX, deltaY = endY - startY;
    // 以垂直移動為主時視為捲動，不處理
    if (Math.abs(deltaX) < THRESHOLD || Math.abs(deltaX) <= Math.abs(deltaY)) return;

    if (deltaX < 0) {
      // 滑開當下才計算按鈕區寬度(按鈕可能在渲染後才設為可見)
      const actions = el.querySelector('.swipe-actions');
      if (actions) el.style.setProperty('--swipe-width', actions.offsetWidth + 'px');
      closeAll(el);
      el.classList.add('swiped');
    } else {
      el.classList.remove('swiped');
    }
  });

  // 點擊按鈕以外的地方時收合
  document.addEventListener('click', function (e) {
    if (e.target.closest('.swipe-action')) return;
    closeAll(null);
  });
})();
