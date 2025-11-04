-- Check which user is logged in and their role
SELECT id, email, role, thematic_area 
FROM users 
WHERE email IN ('braine.kapolon@strathmore.edu', 'kapolonbraine@gmail.com');

-- Check all users with SUPER_ADMIN roles
SELECT id, email, role, thematic_area 
FROM users 
WHERE role LIKE 'SUPER_ADMIN%'
ORDER BY role, email;
