import { createRequire } from 'module';
const require = createRequire(import.meta.url);
const { Eureka } = require('eureka-js-client');

const eurekaHost = process.env.EUREKA_HOST || 'eureka-server';
const eurekaPort = parseInt(process.env.EUREKA_PORT || '8761', 10);
const appName = process.env.EUREKA_APP_NAME || 'INTERFACE-PREDICTION-PDM';
const hostname = process.env.EUREKA_HOSTNAME || process.env.HOSTNAME || 'localhost';
const ipAddr = process.env.EUREKA_IP_ADDR || '127.0.0.1';
const portNum = parseInt(process.env.EUREKA_PORT_NUM || '5173', 10);
const vipAddress = process.env.EUREKA_VIP_ADDRESS || 'interface-prediction.pdm.local';
const zone = process.env.EUREKA_ZONE || 'pdm';
const version = process.env.EUREKA_VERSION || '1.0';
const instanceId = process.env.EUREKA_INSTANCE_ID ||
    `${hostname}:${appName}:${portNum}`;

const eurekaUrl = `http://${eurekaHost}:${eurekaPort}`;
console.log(`[Eureka] Configuring client: app=${appName}, url=${eurekaUrl}, instance=${instanceId}`);

const client = new Eureka({
    logLevel: 'WARN',
    eureka: {
        host: eurekaHost,
        port: eurekaPort,
        servicePath: '/eureka/apps',
        maxRetries: 2,
        serverConnectRetryInterval: 2000,
        serverRetries: 3,
        serverQueryDelay: 1000,
        heartbeatInterval: 30000,
    },
    instance: {
        app: appName,
        instanceId: instanceId,
        hostName: hostname,
        ipAddr: ipAddr,
        port: {
            '$': portNum,
            '@enabled': true
        },
        vipAddress: vipAddress,
        secureVipAddress: vipAddress,
        dataCenterInfo: {
            '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
            name: 'MyOwn'
        },
        status: 'UP',
        metadata: {
            version: version,
            zone: zone
        },
    },
    requestTimeout: 10000,
});

let retryCount = 0;
const maxRetries = 5;
const baseDelay = 5000;

function registerWithRetry(error) {
    retryCount++;
    if (retryCount >= maxRetries) {
        console.error(`[Eureka] Registration failed after ${maxRetries} attempts. App will continue running without Eureka registration.`);
        console.error(`[Eureka] Last error: ${error.message || error}`);
        return;
    }

    const delay = baseDelay * Math.pow(2, retryCount - 1);
    console.warn(`[Eureka] Registration attempt ${retryCount}/${maxRetries} failed: ${error.message || error}. Retrying in ${delay / 1000}s...`);

    setTimeout(() => {
        client.start((err) => {
            if (err) {
                registerWithRetry(err);
            } else {
                console.log(`[Eureka] Successfully registered as ${appName} (${instanceId})`);
            }
        });
    }, delay);
}

// Start Eureka client with delayed retry
setTimeout(() => {
    client.start((error) => {
        if (error) {
            console.error(`[Eureka] Initial registration failed: ${error.message || error}`);
            registerWithRetry(error);
        } else {
            console.log(`[Eureka] Successfully registered as ${appName} (${instanceId})`);
        }
    });
}, 5000);

process.on('SIGINT', () => {
    console.log('[Eureka] Shutting down...');
    try {
        client.stop(() => process.exit(0));
    } catch (e) {
        process.exit(0);
    }
});

process.on('SIGTERM', () => {
    console.log('[Eureka] SIGTERM received, shutting down...');
    try {
        client.stop(() => {
            console.log('[Eureka] Deregistered from Eureka server');
            process.exit(0);
        });
    } catch (e) {
        process.exit(0);
    }
});
