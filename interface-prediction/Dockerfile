FROM node:22-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .
RUN npm run build

FROM node:22-alpine AS production

LABEL authors="Nikolas"

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install --omit=dev

COPY --from=builder /app/dist ./dist
COPY src/api/eureka-config.js ./src/api/eureka-config.js

EXPOSE ${HTTP_PORT:-80}

CMD sh -c "npx serve -s dist -p ${HTTP_PORT:-80} & node src/api/eureka-config.js"
