const postForm = document.getElementById("post-form");
const coverImageInput = document.getElementById("coverImage");
const postDefaultLocaleInput = document.getElementById("post-default-locale");
const postLocaleFields = document.getElementById("post-locale-fields");
const postLocaleEmpty = document.getElementById("post-locale-empty");
const contentFormatInputs = document.querySelectorAll('input[name="contentFormat"]');
const contentFormatHint = document.getElementById("content-format-hint");
const postFormTitle = document.getElementById("post-form-title");
const postFormDescription = document.getElementById("post-form-description");
const statusMessage = document.getElementById("status-message");
const dummyPostButton = document.getElementById("dummy-post-button");
const cancelEditButton = document.getElementById("cancel-edit-button");
const submitButton = document.getElementById("submit-button");
const postList = document.getElementById("post-list");
const emptyState = document.getElementById("empty-state");
const navTabs = document.querySelectorAll("[data-view-target]");
const adminViews = document.querySelectorAll("[data-view]");
const imagePreview = document.getElementById("image-preview");
const imagePreviewWrapper = document.getElementById("image-preview-wrapper");
const postLabelOptions = document.getElementById("post-label-options");
const postLabelEmpty = document.getElementById("post-label-empty");

const localeForm = document.getElementById("locale-form");
const localeCodeInput = document.getElementById("locale-code");
const localeNameInput = document.getElementById("locale-name");
const localeFormTitle = document.getElementById("locale-form-title");
const localeFormDescription = document.getElementById("locale-form-description");
const localeStatusMessage = document.getElementById("locale-status-message");
const dummyLocaleButton = document.getElementById("dummy-locale-button");
const cancelLocaleEditButton = document.getElementById("cancel-locale-edit-button");
const localeSubmitButton = document.getElementById("locale-submit-button");
const savedLocaleList = document.getElementById("saved-locale-list");
const localeEmptyState = document.getElementById("locale-empty-state");

const labelForm = document.getElementById("label-form");
const labelDefaultLocaleInput = document.getElementById("label-default-locale");
const labelLocaleFields = document.getElementById("label-locale-fields");
const labelLocaleEmpty = document.getElementById("label-locale-empty");
const labelImageInput = document.getElementById("labelImage");
const labelImagePreview = document.getElementById("label-image-preview");
const labelImagePreviewWrapper = document.getElementById("label-image-preview-wrapper");
const labelFormTitle = document.getElementById("label-form-title");
const labelFormDescription = document.getElementById("label-form-description");
const labelStatusMessage = document.getElementById("label-status-message");
const dummyLabelButton = document.getElementById("dummy-label-button");
const cancelLabelEditButton = document.getElementById("cancel-label-edit-button");
const labelSubmitButton = document.getElementById("label-submit-button");
const savedLabelList = document.getElementById("saved-label-list");
const labelEmptyState = document.getElementById("label-empty-state");

const firebaseConfig = window.firebaseConfig || {};
const appSettings = window.appSettings || {};
const optionalFirebaseConfigKeys = ["storageBucket"];

const missingConfig = Object.entries(firebaseConfig)
  .filter(function (entry) {
    return (
      !optionalFirebaseConfigKeys.includes(entry[0]) &&
      (!entry[1] || String(entry[1]).includes("YOUR_"))
    );
  })
  .map(function (entry) {
    return entry[0];
  });

if (missingConfig.length > 0) {
  setMessage(
    statusMessage,
    `Lengkapi firebase-config.js terlebih dahulu: ${missingConfig.join(", ")}`,
    "error",
  );
}

let db = null;
let availableLocales = [];
let availableLabels = [];
let editingPostId = null;
let editingPostCoverUrl = "";
let editingPostImageName = "";
let editingLabelId = null;
let editingLabelImageUrl = "";
let editingLabelImageName = "";
let editingLocaleCode = null;

if (missingConfig.length === 0 && window.firebase) {
  firebase.initializeApp(firebaseConfig);
  db = firebase.firestore();
}

coverImageInput.addEventListener("input", function () {
  syncUrlPreview(coverImageInput, imagePreview, imagePreviewWrapper);
});
labelImageInput.addEventListener("input", function () {
  syncUrlPreview(labelImageInput, labelImagePreview, labelImagePreviewWrapper);
});
postForm.addEventListener("submit", handlePostSubmit);
labelForm.addEventListener("submit", handleLabelSubmit);
localeForm.addEventListener("submit", handleLocaleSubmit);
postDefaultLocaleInput.addEventListener("change", handlePostDefaultLocaleChange);
cancelEditButton.addEventListener("click", cancelPostEdit);
cancelLabelEditButton.addEventListener("click", cancelLabelEdit);
cancelLocaleEditButton.addEventListener("click", cancelLocaleEdit);
dummyPostButton.addEventListener("click", fillDummyPostForm);
dummyLabelButton.addEventListener("click", fillDummyLabelForm);
dummyLocaleButton.addEventListener("click", fillDummyLocaleForm);
contentFormatInputs.forEach(function (input) {
  input.addEventListener("change", updateContentFormatHint);
});
navTabs.forEach(function (tab) {
  tab.addEventListener("click", function () {
    setActiveView(tab.dataset.viewTarget);
  });
});

if (db) {
  listenLocales();
  listenLabels();
  listenPosts();
} else {
  emptyState.textContent = "Isi firebase-config.js untuk mulai memakai aplikasi.";
  labelEmptyState.textContent = "Isi firebase-config.js untuk mulai memakai aplikasi.";
  localeEmptyState.textContent = "Isi firebase-config.js untuk mulai memakai aplikasi.";
}

updateContentFormatHint();
renderEmptyLocaleState();
setActiveView("content");
setLabelFormMode("create");
setLocaleFormMode("create");

function setActiveView(viewName) {
  navTabs.forEach(function (tab) {
    const isActive = tab.dataset.viewTarget === viewName;
    tab.classList.toggle("active", isActive);
    tab.setAttribute("aria-selected", isActive ? "true" : "false");
  });

  adminViews.forEach(function (view) {
    const isActive = view.dataset.view === viewName;
    view.classList.toggle("active", isActive);
    view.hidden = !isActive;
  });
}

function setMessage(element, message, type) {
  const nextType = type || "";
  element.textContent = message;
  element.className = `status${nextType ? ` ${nextType}` : ""}`;
}

function formatDate(timestamp) {
  if (!timestamp || typeof timestamp.toDate !== "function") {
    return "-";
  }

  return timestamp.toDate().toLocaleString("id-ID");
}

function formatViews(value) {
  const views = Number.isFinite(Number(value)) ? Number(value) : 0;
  return `${views.toLocaleString("id-ID")} views`;
}

function normalizeLocaleCode(value) {
  return value.trim().toLowerCase().replace(/_/g, "-");
}

function isValidLocaleCode(value) {
  return /^[a-z]{2,3}(-[a-z0-9]{2,8})*$/.test(value);
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function getContentFormat() {
  const selectedInput = Array.from(contentFormatInputs).find(function (input) {
    return input.checked;
  });
  return selectedInput && selectedInput.value === "html" ? "html" : "text";
}

function updateContentFormatHint() {
  const contentFormat = getContentFormat();
  const textareas = Array.from(postLocaleFields.querySelectorAll('[data-field="content"]'));

  if (contentFormat === "html") {
    contentFormatHint.textContent =
      "Mode HTML akan menyimpan markup mentah. Gunakan hanya untuk konten admin tepercaya.";
    textareas.forEach(function (textarea) {
      const locale = textarea.dataset.locale || "";
      textarea.placeholder = `<p>Tulis konten HTML untuk ${locale.toUpperCase()}</p>`;
    });
    return;
  }

  contentFormatHint.textContent =
    "Mode text akan menyimpan isi artikel sebagai plain text biasa.";
  textareas.forEach(function (textarea) {
    const locale = textarea.dataset.locale || "";
    textarea.placeholder = `Tulis isi artikel untuk ${locale.toUpperCase()}`;
  });
}

function setPreviewSource(url, previewElement, previewWrapper) {
  if (!url) {
    previewElement.removeAttribute("src");
    previewWrapper.classList.add("hidden");
    return;
  }

  previewElement.src = url;
  previewWrapper.classList.remove("hidden");
}

function syncUrlPreview(inputElement, previewElement, previewWrapper) {
  setPreviewSource((inputElement.value || "").trim(), previewElement, previewWrapper);
}

function deriveImageNameFromUrl(url, fallbackName) {
  if (!url) {
    return "";
  }

  try {
    const parsedUrl = new URL(url);
    const pathname = parsedUrl.pathname || "";
    const lastSegment = pathname.split("/").filter(Boolean).pop();
    return lastSegment || fallbackName || "external-image";
  } catch (_error) {
    return fallbackName || "external-image";
  }
}

function getLocaleKeys(localizedObject) {
  if (!localizedObject || typeof localizedObject !== "object") {
    return [];
  }

  return Object.keys(localizedObject).filter(function (key) {
    return localizedObject[key];
  });
}

function getLocalizedEntry(localizedObject, preferredLocale) {
  const localeKeys = getLocaleKeys(localizedObject);

  if (localeKeys.length === 0) {
    return {
      locale: preferredLocale,
      value: null,
    };
  }

  const fallbackLocales = [preferredLocale, localeKeys[0]].filter(Boolean);
  const selectedLocale =
    fallbackLocales.find(function (locale) {
      return localizedObject && localizedObject[locale];
    }) || localeKeys[0];

  return {
    locale: selectedLocale,
    value: localizedObject[selectedLocale],
  };
}

function getLabelNames(label) {
  if (label && label.names && typeof label.names === "object") {
    return label.names;
  }

  if (label && label.name) {
    const result = {};
    result[label.defaultLocale || "id"] = label.name;
    return result;
  }

  return {};
}

function getLocalizedLabelName(label, preferredLocale) {
  const names = getLabelNames(label);
  const localizedEntry = getLocalizedEntry(
    names,
    preferredLocale || label.defaultLocale || (availableLocales[0] && availableLocales[0].code) || "id",
  );
  return localizedEntry.value || "Label";
}

function getPostPreviewContent(post) {
  if (post && post.locales && typeof post.locales === "object") {
    const localizedEntry = getLocalizedEntry(
      post.locales,
      post.defaultLocale || (availableLocales[0] && availableLocales[0].code) || "id",
    );
    const entry = localizedEntry.value || {};

    return {
      locale: localizedEntry.locale,
      title: entry.title || "",
      content: entry.content || "",
      localeKeys: getLocaleKeys(post.locales),
    };
  }

  return {
    locale: post.defaultLocale || (availableLocales[0] && availableLocales[0].code) || "id",
    title: post.title || "",
    content: post.content || "",
    localeKeys: [],
  };
}

function renderEmptyLocaleState() {
  const hasLocales = availableLocales.length > 0;
  postLocaleEmpty.classList.toggle("hidden", hasLocales);
  labelLocaleEmpty.classList.toggle("hidden", hasLocales);
  postDefaultLocaleInput.disabled = !hasLocales;
  labelDefaultLocaleInput.disabled = !hasLocales;
  submitButton.disabled = !hasLocales;
  labelSubmitButton.disabled = !hasLocales;
}

function captureLocalizedDraft(container, fields) {
  const draft = {};

  fields.forEach(function (field) {
    container.querySelectorAll(`[data-field="${field}"]`).forEach(function (input) {
      const locale = input.dataset.locale;
      const currentValue = input.value || "";

      if (!draft[locale]) {
        draft[locale] = {};
      }

      draft[locale][field] = currentValue;
    });
  });

  return draft;
}

function capturePostDraft() {
  return captureLocalizedDraft(postLocaleFields, ["title", "content"]);
}

function captureLabelDraft() {
  return captureLocalizedDraft(labelLocaleFields, ["name"]);
}

function renderDefaultLocaleOptions(selectElement, preferredValue) {
  const currentValue = preferredValue || selectElement.value;
  selectElement.innerHTML = "";

  if (availableLocales.length === 0) {
    const placeholder = document.createElement("option");
    placeholder.value = "";
    placeholder.textContent = "Tambahkan bahasa terlebih dahulu";
    selectElement.appendChild(placeholder);
    selectElement.value = "";
    return;
  }

  availableLocales.forEach(function (locale) {
    const option = document.createElement("option");
    option.value = locale.code;
    option.textContent = `${locale.name} (${locale.code})`;
    selectElement.appendChild(option);
  });

  const hasCurrent = availableLocales.some(function (locale) {
    return locale.code === currentValue;
  });

  selectElement.value = hasCurrent ? currentValue : availableLocales[0].code;
}

function createLocaleCard(args) {
  const section = document.createElement("section");
  section.className = "locale-card";
  section.innerHTML = `
    <div class="locale-card-head">
      <h3>${escapeHtml(args.title)}</h3>
      <span class="locale-badge">${escapeHtml(args.locale.code)}</span>
    </div>
    <label>
      <span>${escapeHtml(args.titleLabel)}</span>
      <input
        type="text"
        data-locale="${escapeHtml(args.locale.code)}"
        data-field="title"
        placeholder="Masukkan title untuk ${escapeHtml(args.locale.code.toUpperCase())}"
      />
    </label>
    <label>
      <span>${escapeHtml(args.contentLabel)}</span>
      <textarea
        rows="8"
        data-locale="${escapeHtml(args.locale.code)}"
        data-field="content"
      ></textarea>
    </label>
  `;

  section.querySelector('[data-field="title"]').value = (args.values && args.values.title) || "";
  section.querySelector('[data-field="content"]').value = (args.values && args.values.content) || "";
  return section;
}

function createLabelLocaleCard(args) {
  const section = document.createElement("section");
  section.className = "locale-card";
  section.innerHTML = `
    <div class="locale-card-head">
      <h3>${escapeHtml(args.locale.name)}</h3>
      <span class="locale-badge">${escapeHtml(args.locale.code)}</span>
    </div>
    <label>
      <span>Nama label (${escapeHtml(args.locale.code.toUpperCase())})</span>
      <input
        type="text"
        data-locale="${escapeHtml(args.locale.code)}"
        data-field="name"
        placeholder="Nama label untuk ${escapeHtml(args.locale.code.toUpperCase())}"
      />
    </label>
  `;

  section.querySelector('[data-field="name"]').value = (args.values && args.values.name) || "";
  return section;
}

function renderPostLocaleFields(draft) {
  const nextDraft = draft || capturePostDraft();
  postLocaleFields.innerHTML = "";

  availableLocales.forEach(function (locale) {
    postLocaleFields.appendChild(
      createLocaleCard({
        locale: locale,
        title: locale.name,
        titleLabel: `Judul (${locale.code.toUpperCase()})`,
        contentLabel: `Isi artikel (${locale.code.toUpperCase()})`,
        values: nextDraft[locale.code],
      }),
    );
  });

  updateContentFormatHint();
}

function renderLabelLocaleFields(draft) {
  const nextDraft = draft || captureLabelDraft();
  labelLocaleFields.innerHTML = "";

  availableLocales.forEach(function (locale) {
    labelLocaleFields.appendChild(
      createLabelLocaleCard({
        locale: locale,
        values: nextDraft[locale.code],
      }),
    );
  });
}

function handlePostDefaultLocaleChange() {
  renderLabelOptions();
}

function setPostFormMode(mode) {
  const isEdit = mode === "edit";
  editingPostId = isEdit ? editingPostId : null;
  postFormTitle.textContent = isEdit ? "Edit Artikel" : "Artikel Baru";
  postFormDescription.textContent = isEdit
    ? "Ubah field artikel lalu simpan untuk memperbarui dokumen Firestore yang sama."
    : "Field terjemahan akan muncul otomatis berdasarkan bahasa yang Anda tambahkan dari menu locale.";
  submitButton.textContent = isEdit ? "Update Artikel" : "Simpan Artikel";
  cancelEditButton.classList.toggle("hidden", !isEdit);
}

function setContentFormatSelection(format) {
  contentFormatInputs.forEach(function (input) {
    input.checked = input.value === format;
  });
  updateContentFormatHint();
}

function setSelectedLabelsByIds(labelIds) {
  const nextIds = labelIds || [];
  document.querySelectorAll('input[name="postLabels"]').forEach(function (input) {
    input.checked = nextIds.includes(input.value);
  });
}

function resetPostForm() {
  postForm.reset();
  setPreviewSource("", imagePreview, imagePreviewWrapper);
  editingPostId = null;
  editingPostCoverUrl = "";
  editingPostImageName = "";
  renderDefaultLocaleOptions(postDefaultLocaleInput);
  renderPostLocaleFields({});
  renderLabelOptions([]);
  setSelectedLabelsByIds([]);
  setContentFormatSelection("text");
  setPostFormMode("create");
  syncUrlPreview(coverImageInput, imagePreview, imagePreviewWrapper);
}

function cancelPostEdit() {
  resetPostForm();
  setMessage(statusMessage, "Mode edit dibatalkan.", "info");
}

function startPostEdit(post) {
  const preview = getPostPreviewContent(post);
  const draft = {};
  const postLabelIds = getPostLabelIds(post);

  availableLocales.forEach(function (locale) {
    const localizedEntry = post.locales && post.locales[locale.code] ? post.locales[locale.code] : null;
    draft[locale.code] = {
      title: localizedEntry ? localizedEntry.title || "" : "",
      content: localizedEntry ? localizedEntry.content || "" : "",
    };
  });

  editingPostId = post.id;
  editingPostCoverUrl = post.coverImageUrl || "";
  editingPostImageName = post.imageName || "";
  coverImageInput.value = editingPostCoverUrl;

  renderDefaultLocaleOptions(postDefaultLocaleInput, post.defaultLocale || preview.locale);
  renderPostLocaleFields(draft);
  renderLabelOptions(postLabelIds);
  setSelectedLabelsByIds(postLabelIds);
  setContentFormatSelection(post.contentFormat === "html" ? "html" : "text");
  setPostFormMode("edit");

  setPreviewSource(editingPostCoverUrl, imagePreview, imagePreviewWrapper);

  setActiveView("content");
  window.scrollTo({ top: 0, behavior: "smooth" });
  setMessage(
    statusMessage,
    `Mode edit aktif untuk artikel "${preview.title || "Tanpa judul"}".`,
    "info",
  );
}

async function deletePost(postId) {
  if (!window.confirm("Hapus artikel ini? Tindakan ini tidak bisa dibatalkan.")) {
    return;
  }

  try {
    await db.collection(appSettings.firestoreCollection).doc(postId).delete();

    if (editingPostId === postId) {
      resetPostForm();
    }

    setMessage(statusMessage, "Artikel berhasil dihapus.", "success");
  } catch (error) {
    setMessage(statusMessage, error.message || "Gagal menghapus artikel.", "error");
  }
}

function setLabelFormMode(mode) {
  const isEdit = mode === "edit";
  labelFormTitle.textContent = isEdit ? "Edit Label" : "Tambah Label";
  labelFormDescription.textContent = isEdit
    ? "Perbarui nama terjemahan atau gambar label, lalu simpan untuk mengubah dokumen yang sama."
    : "Nama label akan mengikuti semua locale yang aktif.";
  labelSubmitButton.textContent = isEdit ? "Update Label" : "Simpan Label";
  cancelLabelEditButton.classList.toggle("hidden", !isEdit);
}

function resetLabelForm() {
  labelForm.reset();
  setPreviewSource("", labelImagePreview, labelImagePreviewWrapper);
  editingLabelId = null;
  editingLabelImageUrl = "";
  editingLabelImageName = "";
  renderDefaultLocaleOptions(labelDefaultLocaleInput);
  renderLabelLocaleFields({});
  setLabelFormMode("create");
  syncUrlPreview(labelImageInput, labelImagePreview, labelImagePreviewWrapper);
}

function cancelLabelEdit() {
  resetLabelForm();
  setMessage(labelStatusMessage, "Mode edit label dibatalkan.", "info");
}

function startLabelEdit(label) {
  const names = getLabelNames(label);
  const draft = {};
  const defaultLocale = label.defaultLocale || (availableLocales[0] && availableLocales[0].code) || "id";
  const labelName = getLocalizedLabelName(label, defaultLocale);

  availableLocales.forEach(function (locale) {
    draft[locale.code] = {
      name: names[locale.code] || "",
    };
  });

  editingLabelId = label.id;
  editingLabelImageUrl = label.imageUrl || "";
  editingLabelImageName = label.imageName || "";
  labelImageInput.value = editingLabelImageUrl;

  renderDefaultLocaleOptions(labelDefaultLocaleInput, defaultLocale);
  renderLabelLocaleFields(draft);
  setLabelFormMode("edit");
  setPreviewSource(editingLabelImageUrl, labelImagePreview, labelImagePreviewWrapper);

  setActiveView("label");
  window.scrollTo({ top: 0, behavior: "smooth" });
  setMessage(labelStatusMessage, `Mode edit aktif untuk label "${labelName}".`, "info");
}

async function deleteLabel(labelId) {
  if (!window.confirm("Hapus label ini? Referensi label di artikel lama tidak akan ikut dibersihkan otomatis.")) {
    return;
  }

  try {
    await db.collection(appSettings.labelsCollection).doc(labelId).delete();

    if (editingLabelId === labelId) {
      resetLabelForm();
    }

    setMessage(labelStatusMessage, "Label berhasil dihapus.", "success");
  } catch (error) {
    setMessage(labelStatusMessage, error.message || "Gagal menghapus label.", "error");
  }
}

function setLocaleFormMode(mode) {
  const isEdit = mode === "edit";
  localeFormTitle.textContent = isEdit ? "Edit Bahasa" : "Tambah Bahasa";
  localeFormDescription.textContent = isEdit
    ? "Perbarui kode atau nama bahasa. Jika kode diubah, dokumen locale lama akan dipindahkan ke kode baru."
    : "Locale baru yang ditambahkan di sini akan langsung muncul di form artikel dan label.";
  localeSubmitButton.textContent = isEdit ? "Update Bahasa" : "Simpan Bahasa";
  cancelLocaleEditButton.classList.toggle("hidden", !isEdit);
}

function resetLocaleForm() {
  localeForm.reset();
  editingLocaleCode = null;
  setLocaleFormMode("create");
}

function cancelLocaleEdit() {
  resetLocaleForm();
  setMessage(localeStatusMessage, "Mode edit bahasa dibatalkan.", "info");
}

function startLocaleEdit(locale) {
  editingLocaleCode = locale.code;
  localeCodeInput.value = locale.code || "";
  localeNameInput.value = locale.name || "";
  setLocaleFormMode("edit");
  setActiveView("locale");
  window.scrollTo({ top: 0, behavior: "smooth" });
  setMessage(localeStatusMessage, `Mode edit aktif untuk bahasa "${locale.name}".`, "info");
}

async function deleteLocale(localeCode) {
  if (
    !window.confirm(
      "Hapus bahasa ini? Field terjemahan lama di artikel dan label tidak akan ikut dihapus otomatis.",
    )
  ) {
    return;
  }

  try {
    await db.collection(appSettings.localesCollection).doc(localeCode).delete();

    if (editingLocaleCode === localeCode) {
      resetLocaleForm();
    }

    setMessage(localeStatusMessage, "Bahasa berhasil dihapus.", "success");
  } catch (error) {
    setMessage(localeStatusMessage, error.message || "Gagal menghapus bahasa.", "error");
  }
}

function createDummyCoverUrl(seed) {
  const safeSeed = encodeURIComponent((seed || "dummy-article").toLowerCase().replace(/\s+/g, "-"));
  return `https://picsum.photos/seed/${safeSeed}/1200/630`;
}

function fillDummyLocaleForm() {
  const samples = [
    { code: "id", name: "Indonesia" },
    { code: "en", name: "English" },
    { code: "fr", name: "Francais" },
    { code: "de", name: "Deutsch" },
    { code: "ja", name: "Japanese" },
  ];
  const existingCodes = availableLocales.map(function (locale) {
    return locale.code;
  });
  const nextSample =
    samples.find(function (sample) {
      return !existingCodes.includes(sample.code);
    }) || {
      code: `lang-${existingCodes.length + 1}`,
      name: `Bahasa ${existingCodes.length + 1}`,
    };

  localeCodeInput.value = nextSample.code;
  localeNameInput.value = nextSample.name;
  setMessage(localeStatusMessage, "Dummy locale terisi. Klik Simpan Bahasa untuk menyimpan.", "info");
}

function fillDummyLabelForm() {
  if (availableLocales.length === 0) {
    setMessage(labelStatusMessage, "Tambahkan bahasa dulu sebelum mengisi dummy label.", "error");
    return;
  }

  const labelSamples = {
    id: "Teknologi",
    en: "Technology",
    fr: "Technologie",
    de: "Technologie",
    ja: "Technology",
  };

  renderDefaultLocaleOptions(labelDefaultLocaleInput, availableLocales[0].code);

  labelLocaleFields.querySelectorAll('[data-field="name"]').forEach(function (input) {
    const locale = input.dataset.locale;
    input.value = labelSamples[locale] || `Label Demo ${locale.toUpperCase()}`;
  });

  editingLabelImageUrl = "https://picsum.photos/seed/blog-label-tech/512/512";
  editingLabelImageName = "internet-dummy-label.jpg";
  labelImageInput.value = editingLabelImageUrl;
  setPreviewSource(editingLabelImageUrl, labelImagePreview, labelImagePreviewWrapper);

  setMessage(
    labelStatusMessage,
    "Dummy label terisi dengan gambar dari URL internet. Klik Simpan Label untuk menyimpan.",
    "info",
  );
}

async function fillDummyPostForm() {
  if (availableLocales.length === 0) {
    setMessage(statusMessage, "Tambahkan bahasa dulu sebelum generate dummy artikel.", "error");
    return;
  }

  const textSamples = {
    id: {
      title: "Panduan Memulai Firebase untuk Blog",
      content:
        "Ini adalah contoh konten artikel bahasa Indonesia.\n\nArtikel ini menjelaskan cara memakai Firebase, Firestore, dan Storage untuk blog admin sederhana.",
      html:
        "<p>Ini adalah <strong>contoh konten HTML</strong> bahasa Indonesia.</p><p>Artikel ini menjelaskan cara memakai Firebase, Firestore, dan Storage untuk blog admin sederhana.</p>",
    },
    en: {
      title: "Getting Started with Firebase for a Blog",
      content:
        "This is a sample English article.\n\nIt explains how to use Firebase, Firestore, and Storage for a simple blog admin.",
      html:
        "<p>This is a <strong>sample HTML article</strong> in English.</p><p>It explains how to use Firebase, Firestore, and Storage for a simple blog admin.</p>",
    },
    fr: {
      title: "Demarrage avec Firebase pour un blog",
      content:
        "Ceci est un contenu d'exemple en francais.\n\nIl montre comment utiliser Firebase, Firestore et Storage pour un blog.",
      html:
        "<p>Ceci est un <strong>contenu HTML d'exemple</strong> en francais.</p><p>Il montre comment utiliser Firebase, Firestore et Storage pour un blog.</p>",
    },
  };
  const defaultLocale = postDefaultLocaleInput.value || availableLocales[0].code;
  const localizedFields = {};

  availableLocales.forEach(function (locale) {
    const sample = textSamples[locale.code];
    localizedFields[locale.code] = {
      title: sample ? sample.title : `Demo Article ${locale.code.toUpperCase()}`,
      content: sample
        ? sample.content
        : `Dummy content for ${locale.name} (${locale.code.toUpperCase()}).`,
    };
  });

  const defaultEntry = localizedFields[defaultLocale] || localizedFields[availableLocales[0].code];
  const labels = availableLabels.slice(0, 2).map(function (label) {
    return {
      id: label.id,
      defaultLocale: label.defaultLocale || availableLocales[0].code,
      names: getLabelNames(label),
      imageUrl: label.imageUrl || "",
    };
  });

  dummyPostButton.disabled = true;
  setMessage(statusMessage, "Membuat dummy artikel ke Firestore...", "info");

  try {
    await db.collection(appSettings.firestoreCollection).add({
      defaultLocale: defaultLocale,
      locales: localizedFields,
      contentFormat: "text",
      views: 0,
      labels: labels,
      labelIds: labels.map(function (label) {
        return label.id;
      }),
      coverImageUrl: createDummyCoverUrl(defaultEntry.title),
      imageName: "internet-dummy-cover.jpg",
      createdAt: firebase.firestore.FieldValue.serverTimestamp(),
      updatedAt: firebase.firestore.FieldValue.serverTimestamp(),
    });

    setMessage(statusMessage, "Dummy artikel text dengan cover dari URL internet berhasil dibuat.", "success");
  } catch (error) {
    setMessage(statusMessage, error.message || "Gagal membuat dummy artikel.", "error");
  } finally {
    dummyPostButton.disabled = false;
  }
}

function buildLocalizedFields(entries, entityName) {
  const localizedFields = {};

  Object.entries(entries).forEach(function (entry) {
    const locale = entry[0];
    const value = entry[1];
    const title = (value.title || "").trim();
    const content = (value.content || "").trim();

    if (!title && !content) {
      return;
    }

    if (!title || !content) {
      localizedFields.__error = `${entityName} locale ${locale.toUpperCase()} harus mengisi title dan content sekaligus.`;
      return;
    }

    localizedFields[locale] = {
      title: title,
      content: content,
    };
  });

  if (localizedFields.__error) {
    return { error: localizedFields.__error };
  }

  return { localizedFields: localizedFields };
}

function buildLocalizedNames(entries) {
  const names = {};

  Object.entries(entries).forEach(function (entry) {
    const locale = entry[0];
    const value = entry[1];
    const name = (value.name || "").trim();

    if (!name) {
      return;
    }

    names[locale] = name;
  });

  return names;
}

function buildPostLocales() {
  const draft = capturePostDraft();
  const result = buildLocalizedFields(draft, "Artikel");

  if (result.error) {
    return result;
  }

  if (getLocaleKeys(result.localizedFields).length === 0) {
    return { error: "Isi minimal satu locale artikel." };
  }

  const defaultLocale = postDefaultLocaleInput.value;

  if (!result.localizedFields[defaultLocale]) {
    return {
      error: `Locale utama ${defaultLocale.toUpperCase()} harus terisi.`,
    };
  }

  return result;
}

function buildLabelNames() {
  const draft = captureLabelDraft();
  const names = buildLocalizedNames(draft);

  if (getLocaleKeys(names).length === 0) {
    return { error: "Isi minimal satu nama label." };
  }

  const defaultLocale = labelDefaultLocaleInput.value;

  if (!names[defaultLocale]) {
    return {
      error: `Nama label untuk locale utama ${defaultLocale.toUpperCase()} wajib diisi.`,
    };
  }

  return { names: names };
}

function getSelectedLabelIds() {
  return Array.from(document.querySelectorAll('input[name="postLabels"]:checked')).map(function (input) {
    return input.value;
  });
}

function getSelectedLabels() {
  const selectedIds = getSelectedLabelIds();

  return availableLabels
    .filter(function (label) {
      return selectedIds.includes(label.id);
    })
    .map(function (label) {
      return {
        id: label.id,
        defaultLocale: label.defaultLocale || (availableLocales[0] && availableLocales[0].code) || "id",
        names: getLabelNames(label),
        imageUrl: label.imageUrl || "",
      };
    });
}

function getPostLabelIds(post) {
  if (Array.isArray(post.labelIds) && post.labelIds.length > 0) {
    return post.labelIds.slice();
  }

  if (Array.isArray(post.labels)) {
    return post.labels
      .map(function (label) {
        return label && label.id ? label.id : null;
      })
      .filter(Boolean);
  }

  return [];
}

function migrateLocaleMap(map, previousCode, nextCode, entityLabel) {
  if (!map || typeof map !== "object" || !Object.prototype.hasOwnProperty.call(map, previousCode)) {
    return {
      changed: false,
      value: map,
    };
  }

  if (previousCode !== nextCode && Object.prototype.hasOwnProperty.call(map, nextCode)) {
    throw new Error(
      `${entityLabel} sudah memiliki locale ${nextCode.toUpperCase()}, jadi rename dari ${previousCode.toUpperCase()} dibatalkan untuk mencegah data tertimpa.`,
    );
  }

  const nextMap = Object.assign({}, map);
  nextMap[nextCode] = nextMap[previousCode];
  delete nextMap[previousCode];

  return {
    changed: true,
    value: nextMap,
  };
}

function migrateEmbeddedLabels(labels, previousCode, nextCode, entityLabel) {
  if (!Array.isArray(labels) || labels.length === 0) {
    return {
      changed: false,
      value: labels,
    };
  }

  let changed = false;
  const nextLabels = labels.map(function (label, index) {
    if (!label || typeof label !== "object") {
      return label;
    }

    const nextLabel = Object.assign({}, label);
    let labelChanged = false;

    if (nextLabel.defaultLocale === previousCode) {
      nextLabel.defaultLocale = nextCode;
      labelChanged = true;
    }

    const namesResult = migrateLocaleMap(
      nextLabel.names,
      previousCode,
      nextCode,
      `${entityLabel} pada label tertanam #${index + 1}`,
    );

    if (namesResult.changed) {
      nextLabel.names = namesResult.value;
      labelChanged = true;
    }

    if (labelChanged) {
      changed = true;
    }

    return labelChanged ? nextLabel : label;
  });

  return {
    changed: changed,
    value: nextLabels,
  };
}

function createBatchQueue() {
  const batchLimit = 400;
  const pendingCommits = [];
  let currentBatch = db.batch();
  let currentCount = 0;

  function rotateBatchIfNeeded() {
    if (currentCount < batchLimit) {
      return;
    }

    pendingCommits.push(currentBatch.commit());
    currentBatch = db.batch();
    currentCount = 0;
  }

  return {
    update: function (docRef, payload) {
      rotateBatchIfNeeded();
      currentBatch.update(docRef, payload);
      currentCount += 1;
    },
    flush: async function () {
      if (currentCount > 0) {
        pendingCommits.push(currentBatch.commit());
      }

      if (pendingCommits.length > 0) {
        await Promise.all(pendingCommits);
      }
    },
  };
}

async function migrateLocaleReferences(previousCode, nextCode) {
  if (!previousCode || !nextCode || previousCode === nextCode) {
    return;
  }

  const postSnapshot = await db.collection(appSettings.firestoreCollection).get();
  const labelSnapshot = await db.collection(appSettings.labelsCollection).get();
  const batchQueue = createBatchQueue();

  postSnapshot.docs.forEach(function (doc) {
    const data = doc.data();
    const payload = {};
    let changed = false;

    if (data.defaultLocale === previousCode) {
      payload.defaultLocale = nextCode;
      changed = true;
    }

    const localesResult = migrateLocaleMap(
      data.locales,
      previousCode,
      nextCode,
      `Artikel ${doc.id}`,
    );

    if (localesResult.changed) {
      payload.locales = localesResult.value;
      changed = true;
    }

    const labelsResult = migrateEmbeddedLabels(
      data.labels,
      previousCode,
      nextCode,
      `Artikel ${doc.id}`,
    );

    if (labelsResult.changed) {
      payload.labels = labelsResult.value;
      changed = true;
    }

    if (!changed) {
      return;
    }

    payload.updatedAt = firebase.firestore.FieldValue.serverTimestamp();
    batchQueue.update(doc.ref, payload);
  });

  labelSnapshot.docs.forEach(function (doc) {
    const data = doc.data();
    const payload = {};
    let changed = false;

    if (data.defaultLocale === previousCode) {
      payload.defaultLocale = nextCode;
      changed = true;
    }

    const namesResult = migrateLocaleMap(
      data.names,
      previousCode,
      nextCode,
      `Label ${doc.id}`,
    );

    if (namesResult.changed) {
      payload.names = namesResult.value;
      changed = true;
    }

    if (!changed) {
      return;
    }

    payload.updatedAt = firebase.firestore.FieldValue.serverTimestamp();
    batchQueue.update(doc.ref, payload);
  });

  await batchQueue.flush();
}

async function handleLocaleSubmit(event) {
  event.preventDefault();

  if (missingConfig.length > 0) {
    setMessage(localeStatusMessage, "Firebase belum dikonfigurasi.", "error");
    return;
  }

  const previousCode = editingLocaleCode;
  const code = normalizeLocaleCode(localeCodeInput.value);
  const name = localeNameInput.value.trim();

  if (!code || !name) {
    setMessage(localeStatusMessage, "Kode locale dan nama bahasa wajib diisi.", "error");
    return;
  }

  if (!isValidLocaleCode(code)) {
    setMessage(localeStatusMessage, "Format kode locale tidak valid. Contoh: id, en, pt-br.", "error");
    return;
  }

  localeSubmitButton.disabled = true;
  setMessage(localeStatusMessage, previousCode ? "Menyimpan perubahan bahasa..." : "Menyimpan locale...", "info");

  try {
    let previousLocaleSnapshot = null;

    if (previousCode) {
      previousLocaleSnapshot = await db.collection(appSettings.localesCollection).doc(previousCode).get();
    }

    if (previousCode && previousCode !== code) {
      const nextLocaleSnapshot = await db.collection(appSettings.localesCollection).doc(code).get();

      if (nextLocaleSnapshot.exists) {
        throw new Error(`Locale ${code.toUpperCase()} sudah ada. Pakai kode lain agar data tidak tertimpa.`);
      }
    }

    const localePayload = {
      code: code,
      name: name,
      updatedAt: firebase.firestore.FieldValue.serverTimestamp(),
    };

    if (!previousCode) {
      localePayload.createdAt = firebase.firestore.FieldValue.serverTimestamp();
    } else if (previousCode !== code && previousLocaleSnapshot && previousLocaleSnapshot.exists) {
      const previousCreatedAt = previousLocaleSnapshot.data().createdAt;

      if (previousCreatedAt) {
        localePayload.createdAt = previousCreatedAt;
      }
    }

    await db.collection(appSettings.localesCollection).doc(code).set(
      localePayload,
      { merge: true },
    );

    if (previousCode && previousCode !== code) {
      setMessage(
        localeStatusMessage,
        "Memigrasikan referensi locale di artikel dan label...",
        "info",
      );
      await migrateLocaleReferences(previousCode, code);
      await db.collection(appSettings.localesCollection).doc(previousCode).delete();
    }

    resetLocaleForm();
    setMessage(
      localeStatusMessage,
      previousCode ? "Bahasa berhasil diperbarui." : "Bahasa berhasil disimpan.",
      "success",
    );
  } catch (error) {
    setMessage(localeStatusMessage, error.message || "Gagal menyimpan bahasa.", "error");
  } finally {
    localeSubmitButton.disabled = false;
  }
}

async function handlePostSubmit(event) {
  event.preventDefault();

  if (missingConfig.length > 0) {
    setMessage(statusMessage, "Firebase belum dikonfigurasi.", "error");
    return;
  }

  if (availableLocales.length === 0) {
    setMessage(statusMessage, "Tambahkan bahasa terlebih dahulu sebelum membuat artikel.", "error");
    return;
  }

  const coverImageUrlInput = (coverImageInput.value || "").trim();

  if (!coverImageUrlInput && !editingPostId) {
    setMessage(statusMessage, "Cover image URL wajib diisi.", "error");
    return;
  }

  const localesResult = buildPostLocales();

  if (localesResult.error) {
    setMessage(statusMessage, localesResult.error, "error");
    return;
  }

  const labels = getSelectedLabels();
  const contentFormat = getContentFormat();
  const defaultLocale = postDefaultLocaleInput.value;

  submitButton.disabled = true;
  setMessage(
    statusMessage,
    editingPostId ? "Menyimpan perubahan artikel..." : "Menyimpan artikel ke Firestore...",
    "info",
  );

  try {
    const coverImageUrl = coverImageUrlInput || editingPostCoverUrl;
    const imageName = deriveImageNameFromUrl(coverImageUrl, editingPostImageName);

    const payload = {
      defaultLocale: defaultLocale,
      locales: localesResult.localizedFields,
      contentFormat: contentFormat,
      labels: labels,
      labelIds: labels.map(function (label) {
        return label.id;
      }),
      coverImageUrl: coverImageUrl,
      imageName: imageName,
      updatedAt: firebase.firestore.FieldValue.serverTimestamp(),
    };

    if (editingPostId) {
      await db.collection(appSettings.firestoreCollection).doc(editingPostId).update(payload);
      setMessage(statusMessage, "Artikel berhasil diperbarui.", "success");
    } else {
      payload.views = 0;
      payload.createdAt = firebase.firestore.FieldValue.serverTimestamp();
      await db.collection(appSettings.firestoreCollection).add(payload);
      setMessage(statusMessage, "Artikel berhasil disimpan.", "success");
    }

    resetPostForm();
  } catch (error) {
    setMessage(statusMessage, error.message || "Gagal menyimpan artikel.", "error");
  } finally {
    submitButton.disabled = availableLocales.length === 0;
  }
}

async function handleLabelSubmit(event) {
  event.preventDefault();

  if (missingConfig.length > 0) {
    setMessage(labelStatusMessage, "Firebase belum dikonfigurasi.", "error");
    return;
  }

  if (availableLocales.length === 0) {
    setMessage(labelStatusMessage, "Tambahkan bahasa terlebih dahulu sebelum membuat label.", "error");
    return;
  }

  const namesResult = buildLabelNames();

  if (namesResult.error) {
    setMessage(labelStatusMessage, namesResult.error, "error");
    return;
  }

  const imageUrlInput = (labelImageInput.value || "").trim();
  const defaultLocale = labelDefaultLocaleInput.value;

  labelSubmitButton.disabled = true;
  setMessage(labelStatusMessage, editingLabelId ? "Menyimpan perubahan label..." : "Menyimpan label...", "info");

  try {
    const imageUrl = imageUrlInput || editingLabelImageUrl;
    const imageName = deriveImageNameFromUrl(imageUrl, editingLabelImageName);

    const payload = {
      defaultLocale: defaultLocale,
      names: namesResult.names,
      imageUrl: imageUrl,
      imageName: imageName,
      updatedAt: firebase.firestore.FieldValue.serverTimestamp(),
    };

    if (editingLabelId) {
      await db.collection(appSettings.labelsCollection).doc(editingLabelId).update(payload);
      setMessage(labelStatusMessage, "Label berhasil diperbarui.", "success");
    } else {
      payload.createdAt = firebase.firestore.FieldValue.serverTimestamp();
      await db.collection(appSettings.labelsCollection).add(payload);
      setMessage(labelStatusMessage, "Label berhasil disimpan.", "success");
    }

    resetLabelForm();
  } catch (error) {
    setMessage(labelStatusMessage, error.message || "Gagal menyimpan label.", "error");
  } finally {
    labelSubmitButton.disabled = availableLocales.length === 0;
  }
}

function listenLocales() {
  db.collection(appSettings.localesCollection)
    .orderBy("name", "asc")
    .onSnapshot(
      function (snapshot) {
        const postDraft = capturePostDraft();
        const labelDraft = captureLabelDraft();
        const selectedPostDefault = postDefaultLocaleInput.value;
        const selectedLabelDefault = labelDefaultLocaleInput.value;

        availableLocales = snapshot.docs.map(function (item) {
          return Object.assign({ id: item.id }, item.data());
        });

        renderDefaultLocaleOptions(postDefaultLocaleInput, selectedPostDefault);
        renderDefaultLocaleOptions(labelDefaultLocaleInput, selectedLabelDefault);
        renderPostLocaleFields(postDraft);
        renderLabelLocaleFields(labelDraft);
        renderSavedLocales();
        renderLabelOptions();
        renderSavedLabels();
        renderEmptyLocaleState();
      },
      function (error) {
        localeEmptyState.textContent = `Gagal membaca locale: ${error.message}`;
      },
    );
}

function listenLabels() {
  db.collection(appSettings.labelsCollection)
    .orderBy("createdAt", "desc")
    .onSnapshot(
      function (snapshot) {
        availableLabels = snapshot.docs.map(function (item) {
          return Object.assign({ id: item.id }, item.data());
        });

        renderLabelOptions();
        renderSavedLabels();
      },
      function (error) {
        labelEmptyState.textContent = `Gagal membaca label: ${error.message}`;
      },
    );
}

function listenPosts() {
  db.collection(appSettings.firestoreCollection)
    .orderBy("createdAt", "desc")
    .onSnapshot(
      function (snapshot) {
        const posts = snapshot.docs.map(function (item) {
          return Object.assign({ id: item.id }, item.data());
        });

        renderPosts(posts);
      },
      function (error) {
        emptyState.textContent = `Gagal membaca Firestore: ${error.message}`;
      },
    );
}

function renderSavedLocales() {
  savedLocaleList.innerHTML = "";

  if (availableLocales.length === 0) {
    localeEmptyState.textContent = "Belum ada bahasa.";
    localeEmptyState.classList.remove("hidden");
    return;
  }

  localeEmptyState.classList.add("hidden");

  availableLocales.forEach(function (locale) {
    const item = document.createElement("article");
    item.className = "saved-locale-card";
    item.innerHTML = `
      <div class="saved-locale-head">
        <strong>${escapeHtml(locale.name)}</strong>
        <span class="locale-badge">${escapeHtml(locale.code)}</span>
      </div>
      <span class="post-date">${escapeHtml(formatDate(locale.createdAt))}</span>
      <div class="post-card-actions">
        <button class="inline-action edit" type="button" data-locale-action="edit">Edit</button>
        <button class="inline-action delete" type="button" data-locale-action="delete">Delete</button>
      </div>
    `;

    item.querySelector('[data-locale-action="edit"]').addEventListener("click", function () {
      startLocaleEdit(locale);
    });
    item.querySelector('[data-locale-action="delete"]').addEventListener("click", function () {
      deleteLocale(locale.code);
    });

    savedLocaleList.appendChild(item);
  });
}

function renderLabelOptions(selectedIds) {
  const nextSelectedIds = selectedIds || getSelectedLabelIds();
  const displayLocale = postDefaultLocaleInput.value || (availableLocales[0] && availableLocales[0].code) || "id";
  postLabelOptions.innerHTML = "";

  if (availableLabels.length === 0) {
    postLabelEmpty.classList.remove("hidden");
    return;
  }

  postLabelEmpty.classList.add("hidden");

  availableLabels.forEach(function (label) {
    const item = document.createElement("label");
    item.className = "label-option-card";
    const isChecked = nextSelectedIds.includes(label.id) ? "checked" : "";
    const labelName = getLocalizedLabelName(label, displayLocale);
    const imageMarkup = label.imageUrl
      ? `<img src="${escapeHtml(label.imageUrl)}" alt="${escapeHtml(labelName)}" />`
      : `<span class="label-option-placeholder">${escapeHtml(labelName.charAt(0).toUpperCase())}</span>`;

    item.innerHTML = `
      <input type="checkbox" name="postLabels" value="${escapeHtml(label.id)}" ${isChecked} />
      <div class="label-option-visual">
        ${imageMarkup}
      </div>
      <div class="label-option-copy">
        <strong>${escapeHtml(labelName)}</strong>
        <span>${escapeHtml((label.defaultLocale || displayLocale).toUpperCase())}</span>
      </div>
    `;

    postLabelOptions.appendChild(item);
  });
}

function renderSavedLabels() {
  savedLabelList.innerHTML = "";

  if (availableLabels.length === 0) {
    labelEmptyState.textContent = "Belum ada label.";
    labelEmptyState.classList.remove("hidden");
    return;
  }

  labelEmptyState.classList.add("hidden");

  availableLabels.forEach(function (label) {
    const article = document.createElement("article");
    article.className = "saved-label-card";
    const defaultLocale = label.defaultLocale || (availableLocales[0] && availableLocales[0].code) || "id";
    const primaryName = getLocalizedLabelName(label, defaultLocale);
    const names = getLabelNames(label);
    const localeBadges = getLocaleKeys(names)
      .map(function (locale) {
        return `<span class="mini-badge">${escapeHtml(locale.toUpperCase())}: ${escapeHtml(names[locale])}</span>`;
      })
      .join("");

    const imageMarkup = label.imageUrl
      ? `<img src="${escapeHtml(label.imageUrl)}" alt="${escapeHtml(primaryName)}" />`
      : `<div class="saved-label-placeholder">${escapeHtml(primaryName.charAt(0).toUpperCase())}</div>`;

    article.innerHTML = `
      <div class="saved-label-media">${imageMarkup}</div>
      <div class="saved-label-copy">
        <h3>${escapeHtml(primaryName)}</h3>
        <div class="mini-badge-row">${localeBadges}</div>
        <span class="post-date">${escapeHtml(formatDate(label.createdAt))}</span>
        <div class="post-card-actions">
          <button class="inline-action edit" type="button" data-label-action="edit">Edit</button>
          <button class="inline-action delete" type="button" data-label-action="delete">Delete</button>
        </div>
      </div>
    `;

    article.querySelector('[data-label-action="edit"]').addEventListener("click", function () {
      startLabelEdit(label);
    });
    article.querySelector('[data-label-action="delete"]').addEventListener("click", function () {
      deleteLabel(label.id);
    });

    savedLabelList.appendChild(article);
  });
}

function renderPosts(posts) {
  postList.innerHTML = "";

  if (posts.length === 0) {
    emptyState.textContent = "Belum ada artikel.";
    emptyState.classList.remove("hidden");
    return;
  }

  emptyState.classList.add("hidden");

  posts.forEach(function (post) {
    const article = document.createElement("article");
    article.className = "post-card";
    const contentFormat = post.contentFormat === "html" ? "html" : "text";
    const preview = getPostPreviewContent(post);
    const displayLocale = preview.locale || post.defaultLocale || (availableLocales[0] && availableLocales[0].code) || "id";
    const labels = Array.isArray(post.labels) ? post.labels : [];
    const localeBadges = preview.localeKeys
      .map(function (locale) {
        return `<span class="mini-badge">${escapeHtml(locale.toUpperCase())}</span>`;
      })
      .join("");

    article.innerHTML = `
      <img src="${escapeHtml(post.coverImageUrl || "")}" alt="${escapeHtml(preview.title || "Cover article")}" />
      <div class="post-header">
        <h3>${escapeHtml(preview.title || "Tanpa judul")}</h3>
        <span class="content-badge">${escapeHtml(contentFormat)}</span>
      </div>
      <div class="post-meta-row">
        <span class="post-meta-item">${escapeHtml(formatViews(post.views))}</span>
        <span class="post-meta-item">Dibuat: ${escapeHtml(formatDate(post.createdAt))}</span>
        <span class="post-meta-item">Locale utama: ${escapeHtml((post.defaultLocale || displayLocale).toUpperCase())}</span>
      </div>
      <div class="mini-badge-row">${localeBadges}</div>
      <div class="post-label-row"></div>
      <div class="post-content"></div>
      <div class="post-card-actions">
        <button class="inline-action edit" type="button" data-post-action="edit">Edit</button>
        <button class="inline-action delete" type="button" data-post-action="delete">Delete</button>
      </div>
    `;

    const labelRow = article.querySelector(".post-label-row");
    const contentElement = article.querySelector(".post-content");

    labels.forEach(function (label) {
      const labelChip = document.createElement("div");
      labelChip.className = "post-label-chip";

      if (label.imageUrl) {
        const labelImage = document.createElement("img");
        labelImage.src = label.imageUrl;
        labelImage.alt = getLocalizedLabelName(label, displayLocale);
        labelChip.appendChild(labelImage);
      }

      const labelText = document.createElement("span");
      labelText.textContent = getLocalizedLabelName(label, displayLocale);
      labelChip.appendChild(labelText);
      labelRow.appendChild(labelChip);
    });

    if (labels.length === 0) {
      labelRow.classList.add("hidden");
    }

    if (contentFormat === "html") {
      const htmlContent = document.createElement("div");
      htmlContent.className = "post-content-html";
      htmlContent.innerHTML = preview.content || "";
      contentElement.appendChild(htmlContent);
    } else {
      const paragraph = document.createElement("p");
      paragraph.innerHTML = escapeHtml(preview.content || "").replaceAll("\n", "<br />");
      contentElement.appendChild(paragraph);
    }

    article.querySelector('[data-post-action="edit"]').addEventListener("click", function () {
      startPostEdit(post);
    });

    article.querySelector('[data-post-action="delete"]').addEventListener("click", function () {
      deletePost(post.id);
    });

    postList.appendChild(article);
  });
}
