/**
 * 
 */
zk.afterMount(function () {
  document.querySelectorAll('.swipe-container').forEach(function (el) {
    let startX = 0, currentX = 0;

	// 💡 計算 swipe-actions 寬度
    const actions = el.querySelector('.swipe-actions');
    if (actions) {
      const width = actions.offsetWidth + 'px';
      el.style.setProperty('--swipe-width', width);
    }


    el.addEventListener('touchstart', function (e) {
      startX = e.touches[0].clientX;
    });

    el.addEventListener('touchmove', function (e) {
      currentX = e.touches[0].clientX;
    });

    el.addEventListener('touchend', function () {
      let deltaX = currentX - startX;
      if (deltaX < -50) {
        document.querySelectorAll('.swipe-container.swiped').forEach(function (other) {
          other.classList.remove('swiped');
        });
        el.classList.add('swiped');
      } else if (deltaX > 50) {
        el.classList.remove('swiped');
      }
    });
  });

  document.addEventListener('click', function (e) {
    if (e.target.closest('.swipe-action')) return;
    document.querySelectorAll('.swipe-container.swiped').forEach(function (el) {
      el.classList.remove('swiped');
    });
  });
});
