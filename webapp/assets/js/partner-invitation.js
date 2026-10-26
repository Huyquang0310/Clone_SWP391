(function () {
    var modal = document.getElementById("invitation-message-modal");
    document.querySelectorAll(".invitation-message-view").forEach(function (button) {
        button.addEventListener("click", function () {
            document.getElementById("invitation-message-modal-title").textContent = button.dataset.title;
            document.getElementById("invitation-message-modal-body").textContent = button.dataset.message;
            modal.showModal();
        });
    });
    document.getElementById("invitation-message-modal-close").addEventListener("click", function () {
        modal.close();
    });
    modal.addEventListener("click", function (e) {
        if (e.target === modal) {
            modal.close();
        }
    });
    document.querySelectorAll("form[data-confirm]").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            if (!window.confirm(form.dataset.confirm)) {
                event.preventDefault();
            }
        });
    });
})();
(function () {
    var table = document.querySelector(".partner-invitation-table");
    if (!table) {
        return;
    }

    function widthOf(selector) {
        var cell = table.querySelector("th" + selector);
        return cell ? cell.getBoundingClientRect().width : 0;
    }

    function syncFrozenOffsets() {
        table.style.setProperty("--freeze-status-width", widthOf(".freeze-status") + "px");
        table.style.setProperty("--freeze-actions-width", widthOf(".freeze-actions") + "px");
    }

    window.addEventListener("load", syncFrozenOffsets);
    window.addEventListener("resize", syncFrozenOffsets);
})();

(function () {
    var panel = document.getElementById("invitation-filter-panel");
    if (!panel) {
        return;
    }
    var storageKey = "invitationFilterPanelOpen";
    try {
        panel.open = localStorage.getItem(storageKey) === "true";
    } catch (error) {
        return;
    }
    panel.addEventListener("toggle", function () {
        try {
            localStorage.setItem(storageKey, String(panel.open));
        } catch (error) {
            // Storage unavailable: keep default behavior.
        }
    });
})();
