const sidebarItems = document.querySelectorAll('.sidebar li');
const mainContent = document.getElementById('mainContent');

sidebarItems.forEach(item => {
    item.addEventListener('click', function() {
        const categoryId = this.id;
        const categoryName = this.textContent;

        // Remove 'active' class from all sidebar items
        sidebarItems.forEach(item => {
            item.classList.remove('active');
        });

        // Add 'active' class to the clicked sidebar item
        this.classList.add('active');

        // Update the main content based on the selected category
        mainContent.innerHTML = `<h2>${categoryName}</h2><p>Содержимое ${categoryName.toLowerCase()}</p>`;
    });
});
