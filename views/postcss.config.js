const IN_PRODUCTION = process.env.NODE_ENV === "production";

module.exports = {
  plugins: {
    '0': false,
    tailwindcss: {},
    autoprefixer: {}
  },
};
