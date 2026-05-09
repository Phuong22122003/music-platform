// tailwind.config.js
module.exports = {
  content: [
    "./src/**/*.{html,ts}", // đảm bảo Tailwind scan đúng file Angular
  ],
  theme: {
    extend: {},
  },
  plugins: [
    require("@tailwindcss/line-clamp"), // 👈 thêm plugin ở đây
  ],
};
