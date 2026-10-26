(function () {
    var menus = [];

    function closeMenu(menu) {
        if (!menu.isOpen) {
            return;
        }
        menu.isOpen = false;
        menu.trigger.setAttribute("aria-expanded", "false");
        menu.panel.classList.remove("action-menu__items--open");
        menu.placeholder.parentNode.insertBefore(menu.panel, menu.placeholder);
        menu.panel.style.top = "";
        menu.panel.style.left = "";
    }

    function positionMenu(menu) {
        var anchor = menu.trigger.getBoundingClientRect();
        var panel = menu.panel;
        var panelRect = panel.getBoundingClientRect();
        var left = Math.max(8, Math.min(anchor.right - panelRect.width, window.innerWidth - panelRect.width - 8));
        var top = anchor.bottom + 5;
        if (top + panelRect.height > window.innerHeight - 8) {
            top = anchor.top - panelRect.height - 5;
        }
        panel.style.left = left + "px";
        panel.style.top = Math.max(8, top) + "px";
    }

    document.querySelectorAll("details.action-menu").forEach(function (details) {
        var trigger = document.createElement("button");
        trigger.type = "button";
        trigger.className = "action-menu__trigger";
        trigger.setAttribute("aria-label", "Thao tác khác");
        trigger.setAttribute("aria-haspopup", "menu");
        trigger.setAttribute("aria-expanded", "false");
        trigger.textContent = "⋯";

        var panel = details.querySelector(".action-menu__items");
        var placeholder = document.createComment("action menu");
        panel.parentNode.insertBefore(placeholder, panel);

        var wrapper = document.createElement("div");
        wrapper.className = "action-menu";
        wrapper.append(trigger);
        details.replaceWith(wrapper);

        var menu = {
            trigger: trigger,
            panel: panel,
            placeholder: placeholder,
            isOpen: false
        };
        menus.push(menu);

        trigger.addEventListener("click", function () {
            if (menu.isOpen) {
                closeMenu(menu);
                return;
            }
            menus.forEach(closeMenu);
            menu.isOpen = true;
            trigger.setAttribute("aria-expanded", "true");
            panel.classList.add("action-menu__items--open");
            document.body.appendChild(panel);
            positionMenu(menu);
        });
    });

    document.addEventListener("click", function (event) {
        menus.forEach(function (menu) {
            if (!menu.trigger.contains(event.target) && !menu.panel.contains(event.target)) {
                closeMenu(menu);
            }
        });
    });

    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            menus.forEach(function (menu) {
                if (menu.isOpen) {
                    closeMenu(menu);
                    menu.trigger.focus();
                }
            });
        }
    });

    window.addEventListener("resize", function () {
        menus.forEach(function (menu) {
            if (menu.isOpen) {
                positionMenu(menu);
            }
        });
    });

    window.addEventListener("scroll", function () {
        menus.forEach(closeMenu);
    }, true);
})();
