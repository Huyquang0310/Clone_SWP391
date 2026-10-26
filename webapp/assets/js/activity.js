(function () {
    var tabButtons = document.querySelectorAll(".activity-tab-btn");
    var tabPanes = document.querySelectorAll(".activity-tab-pane");
    if (!tabButtons.length || !tabPanes.length) {
        return;
    }

    function switchTab(targetId) {
        tabButtons.forEach(function (button) {
            var isMatch = button.getAttribute("data-tab") === targetId;
            button.classList.toggle("active", isMatch);
            button.setAttribute("aria-selected", isMatch ? "true" : "false");
        });
        tabPanes.forEach(function (pane) {
            pane.classList.toggle("active", pane.id === targetId);
        });
    }

    tabButtons.forEach(function (button) {
        button.addEventListener("click", function () {
            var tabId = button.getAttribute("data-tab");
            switchTab(tabId);
            if (window.history && window.history.replaceState) {
                window.history.replaceState(null, null, "#" + tabId);
            }
        });
    });

    var currentHash = window.location.hash.slice(1);
    if (["tab-staff", "tab-info", "tab-partner"].indexOf(currentHash) !== -1) {
        switchTab(currentHash);
    }
})();

(function () {
    var panel = document.getElementById("activity-filter-panel");
    if (!panel) {
        return;
    }
    var storageKey = "activityFilterPanelOpen";
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

document.querySelectorAll(".js-date-input").forEach(function (input) {
    input.addEventListener("input", function () {
        var digits = input.value.replace(/\D/g, "").slice(0, 8);
        var parts = [digits.slice(0, 2), digits.slice(2, 4), digits.slice(4)].filter(Boolean);
        input.value = parts.join("/");
    });
});

document.querySelectorAll("a[data-confirm]").forEach(function (link) {
    link.addEventListener("click", function (event) {
        if (!window.confirm(link.dataset.confirm)) {
            event.preventDefault();
        }
    });
});

document.querySelectorAll("button[data-confirm]").forEach(function (button) {
    button.addEventListener("click", function (event) {
        if (!window.confirm(button.dataset.confirm)) {
            event.preventDefault();
        }
    });
});

document.querySelectorAll("form[data-confirm]").forEach(function (form) {
    form.addEventListener("submit", function (event) {
        if (!window.confirm(form.dataset.confirm)) {
            event.preventDefault();
        }
    });
});

(function () {
    var toolbar = document.getElementById("bulk-activity-approval");
    if (!toolbar) {
        return;
    }
    var selectAll = document.querySelector("[data-select-all-activities]");
    var selectionCount = toolbar.querySelector("[data-selection-count]");
    var actionButtons = toolbar.querySelectorAll("button[type=submit]");
    var checkboxes = Array.from(document.querySelectorAll("[data-activity-select]"));

    function updateSelection() {
        var selectedCount = checkboxes.filter(function (checkbox) {
            return checkbox.checked;
        }).length;
        actionButtons.forEach(function (button) {
            button.disabled = selectedCount === 0;
        });
        if (selectAll) {
            selectAll.disabled = checkboxes.length === 0;
        }
        selectionCount.textContent = selectedCount === 0
            ? "Chọn hoạt động đang chờ duyệt"
            : "Đã chọn " + selectedCount + " hoạt động";
        if (selectAll) {
            selectAll.checked = selectedCount > 0 && selectedCount === checkboxes.length;
            selectAll.indeterminate = selectedCount > 0 && selectedCount < checkboxes.length;
        }
    }

    checkboxes.forEach(function (checkbox) {
        checkbox.addEventListener("change", updateSelection);
    });
    if (selectAll) {
        selectAll.addEventListener("change", function () {
            checkboxes.forEach(function (checkbox) {
                checkbox.checked = selectAll.checked;
            });
            updateSelection();
        });
    }
    updateSelection();
})();

document.querySelectorAll("[data-multi-select]").forEach(function (combobox) {
    var trigger = combobox.querySelector(".multi-select-trigger");
    var label = combobox.querySelector("[data-selection-label]");
    var dropdown = combobox.querySelector(".multi-select-dropdown");
    var search = combobox.querySelector(".multi-select-search");
    var optionList = combobox.querySelector(".multi-select-options");
    var emptyState = combobox.querySelector(".multi-select-empty");
    var count = combobox.querySelector(".multi-select-count");
    var initialLabel = label.textContent.trim();
    var itemLabel = combobox.dataset.itemLabel;
    var isSingle = combobox.hasAttribute("data-single");

    function getOptions() {
        return Array.from(combobox.querySelectorAll(".multi-select-option"));
    }

    function closeDropdown() {
        dropdown.hidden = true;
        trigger.setAttribute("aria-expanded", "false");
        combobox.classList.remove("is-open");
        combobox.classList.remove("opens-up");
    }

    function updateSelection() {
        var selectedOptions = getOptions().filter(function (option) {
            return option.querySelector("input").checked;
        });
        var selectedCount = selectedOptions.length;
        combobox.classList.remove("is-invalid");
        if (selectedCount === 0) {
            label.textContent = initialLabel;
            count.textContent = "Chưa chọn " + itemLabel;
        } else if (isSingle) {
            label.textContent = selectedOptions[0].querySelector("span").textContent.trim();
            count.textContent = "Đã chọn " + itemLabel;
        } else {
            label.textContent = selectedOptions.slice(0, 2).map(function (option) {
                return option.querySelector("span").textContent.trim();
            }).join(", ") + (selectedCount > 2 ? " +" + (selectedCount - 2) : "");
            count.textContent = "Đã chọn " + selectedCount + " " + itemLabel;
        }
    }

    function filterOptions() {
        var query = search.value.trim().toLocaleLowerCase();
        var visibleCount = 0;
        getOptions().forEach(function (option) {
            var matches = option.textContent.toLocaleLowerCase().includes(query);
            option.hidden = !matches;
            if (matches) {
                visibleCount++;
            }
        });
        emptyState.hidden = visibleCount !== 0;
    }

    function positionDropdown() {
        var spaceBelow = window.innerHeight - trigger.getBoundingClientRect().bottom;
        var spaceAbove = trigger.getBoundingClientRect().top;
        combobox.classList.toggle("opens-up", spaceBelow < 280 && spaceAbove > spaceBelow);
    }

    function setDisabled(disabled) {
        combobox.toggleAttribute("data-disabled", disabled);
        trigger.disabled = disabled;
        getOptions().forEach(function (option) {
            option.querySelector("input").disabled = disabled;
        });
        if (disabled) {
            closeDropdown();
        }
    }

    function setOptions(items) {
        var inputName = combobox.dataset.inputName;
        var disabled = trigger.disabled;
        optionList.querySelectorAll(".multi-select-option").forEach(function (option) {
            option.remove();
        });
        items.forEach(function (item) {
            var option = document.createElement("label");
            var input = document.createElement("input");
            var text = document.createElement("span");
            option.className = "multi-select-option";
            input.type = isSingle ? "radio" : "checkbox";
            input.name = inputName;
            input.value = item.id;
            input.disabled = disabled;
            input.addEventListener("change", onOptionChange);
            text.textContent = item.name;
            option.append(input, text);
            optionList.append(option);
        });
        search.value = "";
        filterOptions();
        updateSelection();
    }

    function onOptionChange() {
        updateSelection();
        if (isSingle) {
            closeDropdown();
            trigger.focus();
        }
    }

    function validate() {
        var invalid = combobox.hasAttribute("data-required") && !trigger.disabled
            && !getOptions().some(function (option) {
                return option.querySelector("input").checked;
            });
        combobox.classList.toggle("is-invalid", invalid);
        if (invalid) {
            count.textContent = "Vui lòng chọn " + itemLabel;
            trigger.focus();
        }
        return !invalid;
    }

    trigger.addEventListener("click", function () {
        var isExpanded = trigger.getAttribute("aria-expanded") === "true";
        trigger.setAttribute("aria-expanded", String(!isExpanded));
        dropdown.hidden = isExpanded;
        combobox.classList.toggle("is-open", !isExpanded);
        if (!isExpanded) {
            positionDropdown();
            search.focus();
        }
    });

    window.addEventListener("resize", function () {
        if (!dropdown.hidden) {
            positionDropdown();
        }
    });

    search.addEventListener("input", filterOptions);
    getOptions().forEach(function (option) {
        option.querySelector("input").addEventListener("change", onOptionChange);
    });

    combobox.addEventListener("keydown", function (event) {
        if (event.key === "Escape" && !dropdown.hidden) {
            closeDropdown();
            trigger.focus();
        }
    });

    document.addEventListener("click", function (event) {
        if (!combobox.contains(event.target) && !dropdown.hidden) {
            closeDropdown();
        }
    });

    var form = combobox.closest("form");
    if (form) {
        form.addEventListener("submit", function (event) {
            if (!validate()) {
                event.preventDefault();
            }
        });
    }

    function setPlaceholder(text) {
        initialLabel = text;
        updateSelection();
    }

    combobox.comboboxApi = {setDisabled: setDisabled, setOptions: setOptions, setPlaceholder: setPlaceholder};
});
(function () {
    var table = document.querySelector(".activity-list-page .activity-table-wrap table");
    if (!table) {
        return;
    }

    function syncStickyColumnWidths() {
        var actionsCells = table.querySelectorAll("tr td.activity-action-column, tr th.activity-action-column");
        var approvalCells = table.querySelectorAll("tr td.activity-status-column, tr th.activity-status-column");
        var actionsWidth = 0;
        var approvalWidth = 0;

        actionsCells.forEach(function (cell) {
            actionsWidth = Math.max(actionsWidth, cell.getBoundingClientRect().width);
        });
        approvalCells.forEach(function (cell) {
            approvalWidth = Math.max(approvalWidth, cell.getBoundingClientRect().width);
        });
        if (actionsWidth > 0 && approvalWidth > 0) {
            table.style.setProperty("--activity-actions-col-width", actionsWidth + "px");
            table.style.setProperty("--activity-approval-col-width", approvalWidth + "px");
        }
    }

    window.addEventListener("load", syncStickyColumnWidths);
    window.addEventListener("resize", syncStickyColumnWidths);
})();

(function () {
    var type = document.getElementById("inviteType");
    var action = document.getElementById("inviteAction");
    var collab = document.getElementById("inviteCollabFields");
    var terminate = document.getElementById("inviteTerminateFields");
    if (!type || !action || !collab || !terminate) {
        return;
    }
    var link = document.getElementById("partnerLinkCombobox");
    var company = document.getElementById("partnerCompanyCombobox");
    var staff = document.getElementById("partnerStaffCombobox");
    if (!link || !company || !staff || !link.comboboxApi || !company.comboboxApi || !staff.comboboxApi) {
        return;
    }

    function sync() {
        var isTerminate = type.value === "terminatePartner";
        action.value = type.value;
        collab.hidden = isTerminate;
        terminate.hidden = !isTerminate;
        link.comboboxApi.setDisabled(!isTerminate);
        company.comboboxApi.setDisabled(isTerminate);
        staff.comboboxApi.setDisabled(isTerminate || !company.querySelector("input:checked"));
    }
    type.addEventListener("change", sync);
    company.addEventListener("change", function () {
        setTimeout(sync, 0);
    });
    sync();
})();

(function () {
    var companyCombobox = document.getElementById("partnerCompanyCombobox");
    var staffCombobox = document.getElementById("partnerStaffCombobox");
    var message = document.getElementById("partnerStaffMessage");
    if (!companyCombobox || !staffCombobox || !message) {
        return;
    }

    var staffApi = staffCombobox.comboboxApi;
    var endpoint = staffCombobox.dataset.staffEndpoint;

    async function loadPartnerStaff(companyId) {
        staffApi.setOptions([]);
        message.textContent = "";
        if (!companyId) {
            return;
        }

        var url = new URL(endpoint, document.baseURI);
        url.searchParams.set("action", "getStaffsByCompany");
        url.searchParams.set("companyId", companyId);
        try {
            var response = await fetch(url.toString());
            if (!response.ok) {
                throw new Error("Không thể tải danh sách nhân viên đối tác.");
            }
            var staffs = await response.json();
            if (companyCombobox.querySelector("input:checked").value === companyId) {
                staffApi.setOptions(staffs);
            }
        } catch (error) {
            message.textContent = error.message;
        }
    }

    companyCombobox.addEventListener("change", function (event) {
        loadPartnerStaff(event.target.value);
    });
})();

(function () {
    var modal = document.getElementById("activity-message-modal");
    if (!modal) {
        return;
    }
    document.querySelectorAll(".activity-message-view").forEach(function (button) {
        button.addEventListener("click", function () {
            document.getElementById("activity-message-modal-title").textContent = button.dataset.title;
            document.getElementById("activity-message-modal-body").textContent = button.dataset.message;
            modal.showModal();
        });
    });
    document.getElementById("activity-message-modal-close").addEventListener("click", function () {
        modal.close();
    });
    modal.addEventListener("click", function (e) {
        if (e.target === modal) {
            modal.close();
        }
    });
})();

(function () {
    var toggle = document.getElementById("invite-form-toggle");
    var panel = document.getElementById("invite-form-panel");
    if (!toggle || !panel) {
        return;
    }
    toggle.addEventListener("click", function () {
        var open = panel.hidden;
        panel.hidden = !open;
        toggle.setAttribute("aria-expanded", open ? "true" : "false");
    });
})();
