import compression from "compression";
import express from "express";
import { createProxyMiddleware } from "http-proxy-middleware";
import { createRequestHandler } from "@react-router/express";

import * as build from "./build/server/index.js";

const app = express();
const port = Number(process.env.PORT || 3000);

app.disable("x-powered-by");
app.use(compression());
app.use(
  "/api",
  createProxyMiddleware({
    target: process.env.API_ORIGIN || "http://api:8080",
    changeOrigin: false,
  }),
);
app.use("/assets", express.static("build/client/assets", { immutable: true, maxAge: "1y" }));
app.use(express.static("build/client", { maxAge: "1h" }));
app.use(express.static("public", { maxAge: "1h" }));
app.all("/{*splat}", createRequestHandler({ build, mode: process.env.NODE_ENV }));

app.listen(port, "0.0.0.0", () => {
  console.log(`Unified web listening on port ${port}`);
});
