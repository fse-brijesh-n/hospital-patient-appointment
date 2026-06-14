 Auth Service – Checklist (Spring Security Version)
User entity with roles PATIENT, DOCTOR, HOSPITAL_ADMIN, SUPER_ADMIN

Spring Security dependency

Password encoding with BCryptPasswordEncoder bean

JWT utility class (create, validate, extract claims)

JwtAuthenticationFilter (OncePerRequestFilter) to set SecurityContext

SecurityConfig with stateless session, disabled CSRF, route-based access

Super Admin seeded at startup

Patient self‑registration (public)

Login returns JWT access + refresh tokens

Token verification endpoint (public – used by other services)

Refresh token rotation + logout

Admin user creation (only SUPER_ADMIN)

📡 APIs (unchanged)
Method	Endpoint	Access	Description
POST	/auth/register	Public	Patient self‑registration
POST	/auth/admin/create-user	SUPER_ADMIN	Create any user (PATIENT, DOCTOR, HOSPITAL_ADMIN)
POST	/auth/login	Public	Login
POST	/auth/verify	Public	Verify JWT
POST	/auth/refresh	Public	Refresh access token
POST	/auth/logout	Authenticated	Invalidate refresh token