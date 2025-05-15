export const environment = {
    production: false,
    hmr: false,
    http: {
        apiUrl: 'http://localhost:8080/bridge-api',
    },
    mqtt: {
        server: 'localhost',
        protocol: "ws",
        port: 9001
    }
};
