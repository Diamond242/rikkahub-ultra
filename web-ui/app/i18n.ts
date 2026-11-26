import i18n from "i18next";
import { initReactI18next } from "react-i18next";

import enUSCommon from "./locales/en-US/common.json";
import enUSInput from "./locales/en-US/input.json";
import enUSMarkdown from "./locales/en-US/markdown.json";
import enUSMessage from "./locales/en-US/message.json";
import enUSPage from "./locales/en-US/page.json";
import faIRCommon from "./locales/fa-IR/common.json";
import faIRInput from "./locales/fa-IR/input.json";
import faIRMarkdown from "./locales/fa-IR/markdown.json";
import faIRMessage from "./locales/fa-IR/message.json";
import faIRPage from "./locales/fa-IR/page.json";
import zhCNCommon from "./locales/zh-CN/common.json";
import zhCNInput from "./locales/zh-CN/input.json";
import zhCNMarkdown from "./locales/zh-CN/markdown.json";
import zhCNMessage from "./locales/zh-CN/message.json";
import zhCNPage from "./locales/zh-CN/page.json";

const SUPPORTED_LANGUAGES = ["zh-CN", "en-US", "fa-IR"] as const;

const RTL_LANGUAGES = ["fa", "ar", "he", "ur"];

export function isRtlLanguage(language: string | undefined): boolean {
  if (!language) return false;
  return RTL_LANGUAGES.includes(language.split("-")[0].toLowerCase());
}

function applyDocumentDirection(language: string | undefined) {
  if (typeof document === "undefined" || !language) return;
  const root = document.documentElement;
  root.lang = language;
  root.dir = isRtlLanguage(language) ? "rtl" : "ltr";
}

function getInitialLanguage(): (typeof SUPPORTED_LANGUAGES)[number] {
  if (typeof window === "undefined") {
    return "zh-CN";
  }

  const fromStorage = window.localStorage.getItem("lang");
  if (fromStorage === "zh-CN" || fromStorage === "en-US" || fromStorage === "fa-IR") {
    return fromStorage;
  }

  const browserLanguage = window.navigator.language;
  if (browserLanguage.startsWith("zh")) return "zh-CN";
  if (browserLanguage.startsWith("fa")) return "fa-IR";
  return "en-US";
}

void i18n.use(initReactI18next).init({
  resources: {
    "zh-CN": {
      common: zhCNCommon,
      input: zhCNInput,
      markdown: zhCNMarkdown,
      message: zhCNMessage,
      page: zhCNPage,
    },
    "en-US": {
      common: enUSCommon,
      input: enUSInput,
      markdown: enUSMarkdown,
      message: enUSMessage,
      page: enUSPage,
    },
    "fa-IR": {
      common: faIRCommon,
      input: faIRInput,
      markdown: faIRMarkdown,
      message: faIRMessage,
      page: faIRPage,
    },
  },
  lng: getInitialLanguage(),
  fallbackLng: "zh-CN",
  supportedLngs: [...SUPPORTED_LANGUAGES],
  defaultNS: "common",
  ns: ["common", "input", "markdown", "message", "page"],
  interpolation: {
    escapeValue: false,
  },
});

void i18n.on("languageChanged", (language) => {
  if (typeof window !== "undefined") {
    window.localStorage.setItem("lang", language);
  }
  applyDocumentDirection(language);
});

applyDocumentDirection(i18n.language);

export default i18n;
