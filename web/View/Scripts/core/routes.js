import { initLoginPage } from "../pages/login.js";
import { initResultPage } from "../pages/result.js";
import { initCalculatorPage } from "../pages/calculator.js";

export const routes = [
  {
    path: "#/dang-nhap",
    template: "./Pages/login.html",
    controller: initLoginPage,
    title: "Đăng nhập",
    showNav: false,
    requiresAuth: false,
  },
  {
    path: "#/ket-qua",
    template: "./Pages/result.html",
    controller: initResultPage,
    title: "Kết quả xét tuyển",
    showNav: true,
    requiresAuth: true,
  },
  {
    path: "#/tinh-diem",
    template: "./Pages/calculator.html",
    controller: initCalculatorPage,
    title: "Tính điểm xét tuyển",
    showNav: true,
    requiresAuth: true,
  },
];
