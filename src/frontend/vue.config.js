// vue.config.js
module.exports = {
  // https://cli.vuejs.org/config/#devserver-proxy
  devServer: {
    port: 3000,
    //Whenever /api is used, the Spring Boot server will be targeted
    proxy: {
      '/api': {
        target: 'http://localhost:9999',
        ws: true,
        changeOrigin: true
      }
    }
  }
}
