import { authenticate } from "../logic/auth.js";
import { store } from "../core/store.js";
import { navigate } from "../core/router.js";
import { renderComponent } from "../ui/components.js";
import { qs } from "../utils/dom.js";
import { validateCccd, validateDob } from "../utils/validators.js";

const LOADING_DELAY = 350;

export async function initLoginPage() {
  const form = qs("#loginForm");
  if (!form) {
    return;
  }

  const cccdInput = qs("#cccd");
  const dobInput = qs("#dob");
  const submitBtn = qs("#loginSubmit");
  const spinner = qs("#loginSpinner");
  const alertSlot = qs('[data-slot="alert"]');
  const errorCccd = qs('[data-error-for="cccd"]');
  const errorDob = qs('[data-error-for="dob"]');

  const setFieldError = (input, errorEl, message) => {
    if (input) {
      input.classList.add("is-invalid");
    }
    if (errorEl) {
      errorEl.textContent = message;
    }
  };

  const clearFieldError = (input, errorEl) => {
    if (input) {
      input.classList.remove("is-invalid");
    }
    if (errorEl) {
      errorEl.textContent = "";
    }
  };

  const setLoading = (isLoading) => {
    if (submitBtn) {
      submitBtn.disabled = isLoading;
    }
    if (spinner) {
      spinner.classList.toggle("d-none", !isLoading);
    }
  };

  form.addEventListener("submit", async (event) => {
    event.preventDefault();

    clearFieldError(cccdInput, errorCccd);
    clearFieldError(dobInput, errorDob);
    if (alertSlot) {
      alertSlot.innerHTML = "";
    }

    const cccdValue = cccdInput ? cccdInput.value.trim() : "";
    const dobValue = dobInput ? dobInput.value.trim() : "";

    const cccdCheck = validateCccd(cccdValue);
    const dobCheck = validateDob(dobValue);

    if (!cccdCheck.valid) {
      setFieldError(cccdInput, errorCccd, cccdCheck.message);
    }

    if (!dobCheck.valid) {
      setFieldError(dobInput, errorDob, dobCheck.message);
    }

    if (!cccdCheck.valid || !dobCheck.valid) {
      return;
    }

    setLoading(true);
    await new Promise((resolve) => setTimeout(resolve, LOADING_DELAY));

    try {
      const candidate = await authenticate(cccdValue, dobValue);
      if (!candidate) {
        if (alertSlot) {
          alertSlot.innerHTML = await renderComponent("alert", {
            type: "danger",
            title: "Đăng nhập thất bại",
            message: "Không tìm thấy thông tin hoặc mật khẩu không đúng.",
          });
        }
        setLoading(false);
        return;
      }

      store.setState({ session: candidate });
      setLoading(false);
      navigate("#/ket-qua");
    } catch (error) {
      if (alertSlot) {
        alertSlot.innerHTML = await renderComponent("alert", {
          type: "danger",
          title: "Không thể kết nối",
          message: "Vui lòng kiểm tra lại hệ thống và thử lại sau.",
        });
      }
      setLoading(false);
    }
  });
}
