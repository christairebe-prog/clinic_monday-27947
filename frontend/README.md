# Clinic Office Management Frontend

This frontend is based on the newly supplied `frontendclinic.zip` project.

## Assignment features
- Display all offices from the Clinic Final backend.
- Search by office name or office number (case-insensitive, partial match).
- Delete an office after confirmation.
- Show Update but keep it disabled because Update is for the next class.

## Existing backend endpoints
- GET `http://localhost:8083/api/office/all`
- DELETE `http://localhost:8083/api/office/delete/{id}`

The Vite development proxy forwards `/api` requests to the existing backend on port 8083. No backend files are included or replaced.

## Run on Windows
1. Place this folder as `frontend` inside your existing `clinic_final` folder.
2. Start the Spring Boot backend.
3. Open a terminal in this folder.
4. Run `npm.cmd install`.
5. Run `npm.cmd run dev`.
6. Open the local URL printed by Vite, normally `http://localhost:5173`.
