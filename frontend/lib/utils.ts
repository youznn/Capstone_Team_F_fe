import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import ky, { TimeoutError } from "ky";
import { baseUrl } from "./constants";
import { auth } from "./auth";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export const fetcher = ky.create({
  prefixUrl: baseUrl,
  throwHttpErrors: false,
  retry: 0,
  timeout: 5000,
  hooks: {
    beforeError: [
      (error) => {
        if (error instanceof TimeoutError) {
          console.error("Request timed out. Please try again later.");
        }
        return error;
      },
    ],
  },
});

export const fetcherWithAuth = fetcher.extend({
  hooks: {
    beforeRequest: [
      async (request) => {
        // Add Bearer token to Authorization header if user is logged in.
        const session = await auth();
        if (session) {
          request.headers.set(
            "Authorization",
            `Bearer ${session.token.accessToken}`,
          );
        }
      },
    ],
    afterResponse: [
      // Retry if the response status is 401 (unauthorized).
      async (request, options, response) => {
        if (response.status === 401) {
          const session = await auth();
          if (session) {
            request.headers.set(
              "Authorization",
              `Bearer ${session.token.accessToken}`,
            );
            fetcher(request, {
              ...options,
              hooks: {}, // Remove hooks to prevent infinite loop.
            });
          }
        }
      },
    ],
  },
});
