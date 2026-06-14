"# hospital-patient-appointment" 

Checklists & API Lists
Service Dependencies & Sequence

1. auth-service
2. hospital-service
3. patient-service + doctor-service (parallel)
4. appointment-service
5. ehr-service + prescription-service (parallel)
6. billing-service
7. notification-service
Each service is independent in code, but they share JWT validation and sometimes call each other via REST.

🗂️ Master Checklist for All 9 Services
``` #	Service	Key Checklist	APIs
1	auth-service	User entity (email, password hash, role), BCrypt, JWT generation & validation, refresh tokens, patient self‑reg, admin user creation, seeded SUPER_ADMIN	6 APIs (register, login, verify, refresh, logout, admin create user)
2	hospital-service	Hospital entity, auto‑creation of HOSPITAL_ADMIN user during registration, hospital listing (public + admin)	5 APIs (register hospital, list all public, get my hospital, update, list all admin)
3	patient-service	Patient profile, progressive profiling, hospital link	5 APIs (create profile, get own, update, get by ID, list all)
4	doctor-service	Doctor profile, availability slots, internal slot booking	8 APIs (profile CRUD, add slot, get slots, book slot, list doctors)
5	appointment-service	Appointment entity, status lifecycle, call doctor-service to book slot, later Kafka producer	10 APIs (book, patient list, doctor accept/reject/pending/checkin/complete/cancel, view, doctor list, admin list)
6	ehr-service	EHR records, linked to appointment & patient	3 APIs (create, list by patient, get by id)
7	prescription-service	Prescription records, linked to appointment & patient	3 APIs (create, list by patient, get by id)
8	billing-service	Bills, validate EHR & prescription existence before generation	5 APIs (generate bill, patient bills, pay, get by id, admin list)
9	notification-service	Kafka consumer (appointment accepted) + REST test endpoints	4 APIs (manual send, view logs, resend)
🔐 Auth Service – API List (First Service)
Method	Endpoint	Access	Body / Description
POST	/auth/register	Public	{email, password} → registers as PATIENT only
POST	/auth/admin/create-user	SUPER_ADMIN (JWT)	{email, password, role} → creates any user (PATIENT, DOCTOR, HOSPITAL_ADMIN)
POST	/auth/login	Public	{email, password} → returns {accessToken, refreshToken, userId, role}
POST	/auth/verify	Public	{token} → {valid, userId, email, role}
POST	/auth/refresh	Public	{refreshToken} → {accessToken}
POST	/auth/logout	Authenticated (JWT)	Invalidates refresh token```
Now, let's build the auth-service step by step – exactly

