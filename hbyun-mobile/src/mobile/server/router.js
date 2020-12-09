import Router from "koa-router";
import use from "@mdf/plugin-meta/lib/router";

const router = Router();

use(router);

router.get("/view/:billtype/:billno", function(ctx) {
  ctx.render();
});

router.get("/", function(ctx) {
  ctx.render();
});

export default router;
