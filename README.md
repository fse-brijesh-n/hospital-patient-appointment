API Documentation – Healthcare Microservices System
This document lists every endpoint with its HTTP method, URL, access role, request body (where applicable), and example response.
All services return JSON. Authentication uses a JWT passed in the Authorization: Bearer <token> header.

1. Auth Service (port 8001)
   POST /auth/register
   Access: Public

Description: Patient self‑registration (role forced to PATIENT)

Request

json
{
"email": "patient@example.com",
"password": "secret123"
}
Response 201 Created

json
{
"userId": "c3d4e5f6-...",
"email": "patient@example.com",
"role": "PATIENT",
"accessToken": "eyJ...",
"refreshToken": "eyJ..."
}
POST /auth/login
Access: Public

Request

json
{
"email": "admin@healthcare.com",
"password": "Admin@123"
}
Response 200 OK

json
{
"userId": "bf46d651-...",
"email": "admin@healthcare.com",
"role": "SUPER_ADMIN",
"accessToken": "eyJhbGciOiJIUzUxMiJ9...",
"refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
POST /auth/verify
Access: Public (used internally by other services)

Request

json
{
"token": "eyJhbGciOiJIUzUxMiJ9..."
}
Response 200 OK (valid token)

json
{
"valid": true,
"userId": "bf46d651-...",
"email": "admin@healthcare.com",
"role": "SUPER_ADMIN"
}
If token is invalid:

json
{
"valid": false,
"userId": null,
"email": null,
"role": null
}
POST /auth/refresh
Access: Public

Request

json
{
"refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
Response 200 OK – new pair of tokens (old refresh token invalidated)

json
{
"userId": "bf46d651-...",
"email": "admin@healthcare.com",
"role": "SUPER_ADMIN",
"accessToken": "eyJ...",
"refreshToken": "eyJ..."
}
POST /auth/logout
Access: Authenticated (any role)

Request

json
{
"refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
}
Response 200 OK (empty body)

POST /auth/admin/create-user
Access: SUPER_ADMIN only

Request

json
{
"email": "dr.smith@example.com",
"password": "doc123",
"role": "DOCTOR"
}
Response 201 Created

json
{
"userId": "402a4481-...",
"email": "dr.smith@example.com",
"role": "DOCTOR",
"accessToken": "eyJ...",
"refreshToken": "eyJ..."
}
2. Hospital Service (port 8002)
   POST /hospitals
   Access: SUPER_ADMIN

Description: Register hospital and auto‑create its HOSPITAL_ADMIN user.

Request

json
{
"name": "City General Hospital",
"address": "123 Main St",
"phone": "+1234567890",
"email": "info@cityhospital.com",
"adminEmail": "admin@cityhospital.com",
"adminPassword": "Admin@123"
}
Response 201 Created

json
{
"hospitalId": "d4e5f6a7-...",
"name": "City General Hospital",
"address": "123 Main St",
"phone": "+1234567890",
"email": "info@cityhospital.com",
"adminUserId": "a1b2c3d4-..."
}
GET /hospitals
Access: Public

Response 200 OK

json
[
{
"id": "d4e5f6a7-...",
"name": "City General Hospital",
"address": "123 Main St",
"phone": "+1234567890"
}
]
GET /hospitals/my
Access: HOSPITAL_ADMIN

Response 200 OK

json
{
"id": "d4e5f6a7-...",
"name": "City General Hospital",
"address": "123 Main St",
"phone": "+1234567890",
"email": "info@cityhospital.com"
}
GET /hospitals/{hospitalId}
Access: SUPER_ADMIN, HOSPITAL_ADMIN (own hospital only)

Response same as above

PUT /hospitals/{hospitalId}
Access: HOSPITAL_ADMIN (own hospital)

Request

json
{
"address": "456 New Address"
}
Response 200 OK with updated hospital details.

GET /hospitals/{hospitalId}/doctors
Access: HOSPITAL_ADMIN (own hospital)

Response 200 OK – array of doctor profiles (same structure as doctor-service response).

GET /hospitals/{hospitalId}/patients
Access: HOSPITAL_ADMIN (own hospital)

Response 200 OK – array of patient profiles.

3. Patient Service (port 8003)
   POST /patients/me
   Access: PATIENT

Description: Create minimal profile (progressive profiling step 1).

Request

json
{
"hospitalId": "d4e5f6a7-...",
"email": "patient@example.com",
"firstName": "John",
"lastName": "Doe",
"phone": "+1234567890"
}
Response 201 Created

json
{
"id": "p1a2t3i4-...",
"userId": "c3d4e5f6-...",
"hospitalId": "d4e5f6a7-...",
"email": "patient@example.com",
"firstName": "John",
"lastName": "Doe",
"phone": "+1234567890",
"profileComplete": false,
"createdAt": "2026-06-14T20:30:00"
}
GET /patients/me
Access: PATIENT

Response 200 OK – full patient profile.

PATCH /patients/me
Access: PATIENT

Description: Complete profile (progressive profiling step 2).

Request

json
{
"dateOfBirth": "1990-05-15",
"address": "123 Patient St",
"emergencyContact": "+9876543210",
"bloodType": "O+",
"allergies": "None",
"medicalHistory": "Asthma"
}
Response 200 OK with profileComplete: true (if all mandatory fields are now filled).

GET /patients/{patientId}
Access: DOCTOR (same hospital), HOSPITAL_ADMIN (own hospital), SUPER_ADMIN

Response 200 OK – full patient profile.

GET /patients
Access: HOSPITAL_ADMIN (own hospital), SUPER_ADMIN

Response 200 OK – page of patient profiles (supports ?hospitalId= filter).

4. Doctor Service (port 8004)
   POST /doctors/me
   Access: DOCTOR

Description: Create minimal doctor profile.

Request

json
{
"hospitalId": "d4e5f6a7-...",
"email": "dr.smith@example.com",
"firstName": "Smith",
"lastName": "John",
"phone": "+1122334455"
}
Response 201 Created

json
{
"id": "d1o2c3t4-...",
"userId": "402a4481-...",
"hospitalId": "d4e5f6a7-...",
"email": "dr.smith@example.com",
"firstName": "Smith",
"lastName": "John",
"phone": "+1122334455",
"profileComplete": false,
"createdAt": "2026-06-14T20:35:00"
}
GET /doctors/me
Access: DOCTOR

Response 200 OK – full doctor profile.

PATCH /doctors/me
Access: DOCTOR

Description: Complete profile (specialization, fee, etc.)

Request

json
{
"specialization": "Cardiology",
"licenseNumber": "MED12345",
"consultationFee": 150.00,
"bio": "Experienced cardiologist"
}
Response 200 OK with profileComplete: true.

GET /doctors
Access: Any authenticated user

Query params: hospitalId, specialization

Response 200 OK – page of doctor profiles.

GET /doctors/{doctorId}
Access: Any authenticated

Response 200 OK – single doctor details.

POST /doctors/me/slots
Access: DOCTOR

Request

json
{
"date": "2026-07-20",
"startTime": "10:00",
"endTime": "10:30"
}
Response 201 Created

json
{
"id": "s1l2o3t4-...",
"doctorId": "d1o2c3t4-...",
"date": "2026-07-20",
"startTime": "10:00",
"endTime": "10:30",
"booked": false
}
GET /doctors/{doctorId}/slots
Access: Any authenticated

Response 200 OK – list of slots.

PUT /doctors/slots/{slotId}/book
Access: Internal (called by appointment-service)

Response 200 OK – marks slot as booked. Returns 400 if already booked.

5. Appointment Service (port 8005)
   POST /appointments
   Access: PATIENT

Description: Book an appointment.

Request

json
{
"doctorId": "d1o2c3t4-...",
"appointmentTime": "2026-07-20T10:00:00",
"reason": "Regular checkup"
}
Response 201 Created

json
{
"id": "a1p2p3o4-...",
"patientId": "p1a2t3i4-...",
"doctorId": "d1o2c3t4-...",
"appointmentTime": "2026-07-20T10:00:00",
"status": "REQUESTED",
"reason": "Regular checkup",
"createdAt": "2026-06-14T21:00:00"
}
GET /appointments/patient/me
Access: PATIENT

Response 200 OK – list of own appointments (filterable by ?status=).

PUT /appointments/{id}/cancel
Access: PATIENT or DOCTOR

Response 200 OK – status changed to CANCELLED.

Doctor Status Actions
PUT /appointments/{id}/accept → ACCEPTED
PUT /appointments/{id}/reject → REJECTED (body: {"reason":"..."})
PUT /appointments/{id}/pending → PENDING
PUT /appointments/{id}/checkin → CHECKED_IN
PUT /appointments/{id}/complete → COMPLETED

All return 200 OK with the updated appointment object.

GET /appointments/{id}
Access: Owner patient, linked doctor, any admin

Response 200 OK – full appointment details.

GET /appointments/doctor/me
Access: DOCTOR

Response 200 OK – doctor’s appointments (filterable by ?status=).

GET /appointments
Access: HOSPITAL_ADMIN (own hospital), SUPER_ADMIN

Response 200 OK – page of appointments.

6. EHR Service (port 8006)
   POST /ehr
   Access: DOCTOR

Request

json
{
"appointmentId": "a1p2p3o4-...",
"patientId": "p1a2t3i4-...",
"diagnosis": "Hypertension",
"treatment": "Medication and diet",
"notes": "Follow up in 1 month"
}
Response 201 Created

json
{
"id": "e1h2r3-...",
"appointmentId": "a1p2p3o4-...",
"patientId": "p1a2t3i4-...",
"diagnosis": "Hypertension",
"treatment": "Medication and diet",
"notes": "Follow up in 1 month",
"recordDate": "2026-07-20T10:30:00"
}
GET /ehr/patient/{patientId}
Access: DOCTOR (same hospital), PATIENT (owner)

Response 200 OK – list of EHR records.

GET /ehr/record/{id}
Access: DOCTOR (same hospital), PATIENT (owner)

Response 200 OK – single record.

GET /ehr/by-appointment/{appointmentId}
Access: DOCTOR, ADMIN, billing-service (internal)

Response 200 OK – record linked to the appointment (used to check existence before billing).

7. Prescription Service (port 8007)
   POST /prescriptions
   Access: DOCTOR

Request

json
{
"appointmentId": "a1p2p3o4-...",
"patientId": "p1a2t3i4-...",
"medicines": [
{"name": "Aspirin", "dosage": "100mg", "duration": "30 days"}
],
"notes": "Take after meals"
}
Response 201 Created

json
{
"id": "p1r2e3-...",
"appointmentId": "a1p2p3o4-...",
"patientId": "p1a2t3i4-...",
"medicines": [{"name": "Aspirin", "dosage": "100mg", "duration": "30 days"}],
"notes": "Take after meals",
"prescribedDate": "2026-07-20T10:35:00"
}
GET /prescriptions/patient/{patientId}
Access: DOCTOR (same hospital), PATIENT (owner)

Response 200 OK – list.

GET /prescriptions/{id}
Access: DOCTOR (same hospital), PATIENT (owner)

Response 200 OK – single.

GET /prescriptions/by-appointment/{appointmentId}
Access: DOCTOR, ADMIN, billing-service (internal)

Response 200 OK – used to check existence before billing.

8. Billing Service (port 8008)
   POST /bills
   Access: HOSPITAL_ADMIN (own hospital), SUPER_ADMIN

Description: Generates bill only if EHR and prescription exist for the completed appointment.

Request

json
{
"appointmentId": "a1p2p3o4-...",
"amount": 200.00,
"dueDate": "2026-08-01"
}
Response 201 Created

json
{
"id": "b1i2l3-...",
"appointmentId": "a1p2p3o4-...",
"patientId": "p1a2t3i4-...",
"amount": 200.00,
"status": "PENDING",
"dueDate": "2026-08-01"
}
If EHR or prescription missing → 409 Conflict.

GET /bills/patient/me
Access: PATIENT

Response 200 OK – list of own bills.

GET /bills/patient/{patientId}
Access: HOSPITAL_ADMIN (own hospital), SUPER_ADMIN

Response 200 OK.

PUT /bills/{id}/pay
Access: PATIENT (own bill), ADMIN

Response 200 OK – status changed to PAID.

GET /bills/{id}
Access: Owner patient, admin

Response 200 OK.

9. Notification Service (port 8009)
   POST /notifications/send
   Access: ADMIN

Request

json
{
"toEmail": "patient@example.com",
"toPhone": "+1234567890",
"subject": "Appointment Confirmed",
"message": "Your appointment is confirmed for July 20.",
"channel": "EMAIL"
}
Response 200 OK

json
{
"id": "n1o2t3-...",
"status": "SENT"
}
GET /notifications/logs
Access: ADMIN

Response 200 OK – paginated notification logs.

GET /notifications/logs/{userId}
Access: ADMIN

Response 200 OK – logs for a specific user.

POST /notifications/resend/{notificationId}
Access: ADMIN

Response 200 OK – retries sending.

All responses include standard error format on failure:

json
{
"timestamp": "2026-06-14T21:00:00",
"status": 400,
"error": "Bad Request",
"message": "Error description"
}
This API document reflects the exact contracts to be implemented. The auth-service is already live and matches these specs. The remaining services will be built following this document.

POST /auth/hospital-admin/create-user – for HOSPITAL_ADMIN
This allows a hospital admin to create doctor and patient accounts that belong to their own hospital.
It works exactly like the Super Admin’s create‑user endpoint, but the hospital ID is taken from the logged‑in HOSPITAL_ADMIN’s hospital (no need to pass it in the request).

Method	Endpoint	Access	Request body	Description
POST	/auth/hospital-admin/create-user	HOSPITAL_ADMIN	{"email":"...","password":"...","role":"DOCTOR"} or "role":"PATIENT"	Creates a user (DOCTOR or PATIENT) associated with the hospital admin’s hospital. The hospital_id is stored in the user’s profile later, not in the auth service.
Why we need it:
A hospital admin must be able to onboard their own doctors and patients, not just the Super Admin. This was clearly stated in the requirements.

✅ Everything else is covered
Requirement	Where it's handled
Patient self‑registration	POST /auth/register (only PATIENT role)
Super Admin creates any user	POST /auth/admin/create-user
Hospital Admin creates doctors/patients	POST /auth/hospital-admin/create-user (the missing one above)
Doctor appointment status actions (accept, reject, check‑in, pending, complete)	Appointment service: PUT /appointments/{id}/accept, etc.
Mandatory EHR & Prescription after visit	EHR & Prescription services – billing checks them before allowing bill creation
Admin registers hospital → Hospital Admin auto‑created	POST /hospitals internally calls auth‑service to create the admin user
Kafka only for notifications	Appointment service produces, notification service consumes
All other inter‑service calls are REST	Every service follows this
