FROM node:20-alpine AS builder
WORKDIR /app

COPY package.json ./
RUN npm install

COPY . .

ARG NEXT_PUBLIC_API_BASE_URL=http://localhost:18080/api
ARG NEXT_PUBLIC_AUTH_BASE_URL=http://localhost:18081
ARG NEXT_PUBLIC_OAUTH_CLIENT_ID=everp-spa
ARG NEXT_PUBLIC_OAUTH_REDIRECT_URI=http://localhost:13000/callback

ENV NEXT_PUBLIC_API_BASE_URL=${NEXT_PUBLIC_API_BASE_URL}
ENV NEXT_PUBLIC_AUTH_BASE_URL=${NEXT_PUBLIC_AUTH_BASE_URL}
ENV NEXT_PUBLIC_OAUTH_CLIENT_ID=${NEXT_PUBLIC_OAUTH_CLIENT_ID}
ENV NEXT_PUBLIC_OAUTH_REDIRECT_URI=${NEXT_PUBLIC_OAUTH_REDIRECT_URI}
ENV NEXT_TELEMETRY_DISABLED=1

RUN npm run build

FROM node:20-alpine AS runtime
WORKDIR /app

ENV NODE_ENV=production
ENV NEXT_TELEMETRY_DISABLED=1

COPY --from=builder /app/package.json /app/package.json
COPY --from=builder /app/node_modules /app/node_modules
COPY --from=builder /app/.next /app/.next
COPY --from=builder /app/public /app/public
COPY --from=builder /app/next.config.ts /app/next.config.ts

EXPOSE 3000

CMD ["npm", "run", "start", "--", "-p", "3000"]
