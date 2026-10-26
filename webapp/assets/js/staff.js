document.addEventListener("DOMContentLoaded", function () {

    // =========================
    // 1. Get elements
    // =========================

    const searchInput =
        document.getElementById("staffSearch");

    const statusFilter =
        document.getElementById("staffStatusFilter");

    const rows =
        document.querySelectorAll(".staff-row");

    const emptyMessage =
        document.getElementById("staffEmpty");


    // =========================
    // 2. Filter staff
    // =========================

    function filterStaff() {

        const keyword =
            searchInput
                ? searchInput.value.trim().toLowerCase()
                : "";

        const selectedStatus =
            statusFilter
                ? statusFilter.value.toLowerCase()
                : "";

        let visibleCount = 0;


        rows.forEach(function (row) {

            const rowText =
                row.textContent.toLowerCase();

            const rowStatus =
                (row.dataset.status || "")
                    .toLowerCase();


            // Search theo nội dung
            const matchKeyword =
                keyword === "" ||
                rowText.includes(keyword);


            // Filter status
            const matchStatus =
                selectedStatus === "" ||
                rowStatus === selectedStatus;


            const visible =
                matchKeyword &&
                matchStatus;


            row.style.display =
                visible ? "" : "none";


            if (visible) {
                visibleCount++;
            }
        });


        // Không có kết quả
        if (emptyMessage) {

            emptyMessage.style.display =
                visibleCount === 0
                    ? ""
                    : "none";
        }
    }


    // =========================
    // 3. Events
    // =========================

    if (searchInput) {

        searchInput.addEventListener(
            "input",
            filterStaff
        );
    }


    if (statusFilter) {

        statusFilter.addEventListener(
            "change",
            filterStaff
        );
    }
});