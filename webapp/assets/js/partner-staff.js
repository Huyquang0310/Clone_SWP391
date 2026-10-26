var companyFilter = document.getElementById("companyFilter");
if (companyFilter) {
    companyFilter.addEventListener("change", function () {
        companyFilter.form.submit();
    });
}

document.querySelectorAll("a[data-confirm]").forEach(function (link) {
    link.addEventListener("click", function (event) {
        if (!window.confirm(link.dataset.confirm)) {
            event.preventDefault();
        }
    });
});
