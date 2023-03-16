function filterByLocation(location) {
  const rows = document.querySelectorAll('.tournament-row');
  rows.forEach(row => {
    const rowLocation = row.querySelector('.row-location').textContent;
    if (!location || rowLocation === location) {
      row.style.display = '';
    } else {
      row.style.display = 'none';
    }
  });
}