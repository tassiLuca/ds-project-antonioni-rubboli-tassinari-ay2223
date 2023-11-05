import { Router } from "express";
import {generateCodeFor, invalidate, isValid, verifyCodeOwner} from "../controllers/codes";
import RedisLimiterStorage from "../configs/redis.config";
import {apiLimiter, ApiLimiterEntry} from "core-components";
import {authenticationHandler} from "../middleware/authentication.middleware";

const codesRoute =  Router();

const limitStorage = new RedisLimiterStorage();
const API_LIMITER_RULES: ApiLimiterEntry = {
    "/generate": {
        "POST": {
            time: 20,
            limit: 5
        }
    },

    "/is-valid": {
        "POST": {
            time: 20,
            limit: 20,
        }
    },

    "/invalidate": {
        "PATCH": {
            time: 20,
            limit: 5
        }
    },

    "/verify-owner": {
        "POST": {
            time: 20,
            limit: 20
        }
    }
}
codesRoute.use(apiLimiter(API_LIMITER_RULES, limitStorage));

/**
 * @openapi
 *
 * paths:
 *   /code/generate:
 *      post:
 *          summary: Generate a new voting code
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              userId:
 *                                  type: string
 *                                  description: The id of a user.
 *                              electionId:
 *                                  type: string
 *                                  description: The id of a valid election.
 *                              code:
 *                                  type: string
 *                                  description: The voting code for an election.
 *
 *          responses:
 *              '201':
 *                  description: The election is created successfully.
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
codesRoute.post("/generate", authenticationHandler, generateCodeFor);

/**
 * @openapi
 *
 * paths:
 *   /code/is-valid:
 *      post:
 *          summary: Check if a given code is valid
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              userId:
 *                                  type: string
 *                                  description: The id of the user.
 *                              electionId:
 *                                  type: string
 *                                  description: The id of an election.
 *                              code:
 *                                  type: string
 *                                  description: The code to check
 *          responses:
 *              '200':
 *                  description: The request was handled successfully
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
codesRoute.post("/is-valid", authenticationHandler, isValid);

/**
 * @openapi
 *
 * paths:
 *   /code/verify-owner:
 *      post:
 *          summary: Check if the code belongs to the specified user
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              userId:
 *                                  type: string
 *                                  description: The id of the user.
 *                              electionId:
 *                                  type: string
 *                                  description: The id of an election.
 *                              code:
 *                                  type: string
 *                                  description: The code to check
 *          responses:
 *              '200':
 *                  description: The request was handled successfully
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
codesRoute.post("/verify-owner", authenticationHandler, verifyCodeOwner);

/**
 * @openapi
 *
 * paths:
 *   /code/is-valid:
 *      patch:
 *          summary: invalidate a code
 *          requestBody:
 *              required: true
 *              content:
 *                  application/json:
 *                      schema:
 *                          type: object
 *                          properties:
 *                              userId:
 *                                  type: string
 *                                  description: The id of the user that own the code.
 *                              electionId:
 *                                  type: string
 *                                  description: The id of an election.
 *                              code:
 *                                  type: string
 *                                  description: The code to invalidate
 *          responses:
 *              '200':
 *                  description: The request was handled successfully
 *              '429':
 *                  description: Limit of requests reached for this endpoint.
 *              '500':
 *                  description: Generic server error
 *
 */
codesRoute.patch("/invalidate", authenticationHandler, invalidate);

export default codesRoute;