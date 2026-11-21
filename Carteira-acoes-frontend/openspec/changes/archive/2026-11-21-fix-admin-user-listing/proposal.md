## Why

Administrators cannot see the published user listing in the administrative area, preventing them from accessing the management flow that the current specifications already require. This is a regression/implementation defect, not a new product capability.

## What Changes

- Restore the administrator-protected Users route so it requests and renders the existing paginated user listing.
- Preserve the current loading, empty, error, pagination, role-guard, and accessible user-management behavior.
- Add focused regression coverage for the route's successful listing state and safe failure states.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

None. The existing `user-access-experience` and `investment-api-integration` requirements already define the required behavior; this change restores their implementation.

## Impact

- Affected frontend area: administrative user-management route/page and its existing user service integration.
- API contract: no endpoint, payload, DTO, authorization, or backend change; reuse the published paginated users resource.
- Dependencies: none.
