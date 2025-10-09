# VPS Debugging Steps

## Hiện tại đã có:

### ✅ Local Build Test Success
- Build thành công với VPS debugging enabled
- Tất cả flyway executables được detect và test
- Debug output shows proper system info và paths

### ✅ Debugging Features Added:
1. **System Information**: OS, architecture, Java version
2. **Path Analysis**: Complete PATH và executable locations  
3. **Wrapper Script**: Detailed execution logging với arguments
4. **Cache Busting**: BUILD_DATE để force fresh builds
5. **VPS Detection**: VPS_DEBUG flag để conditional debugging

## Next Steps để Debug trên VPS:

### 1. Push changes lên repository:
```bash
git push origin fix/migration-and-docker-issues
```

### 2. Deploy lên VPS và monitor logs:
```bash
# Trên VPS, pull latest changes
git pull origin fix/migration-and-docker-issues

# Force rebuild với debug enabled
docker-compose build --no-cache migration

# Run migration với debug output
docker-compose up migration

# Hoặc để xem realtime logs:
docker-compose up --build migration | tee vps-debug.log
```

### 3. Key Debug Output để tìm:

#### System Environment:
```
=== VPS DEBUG MODE ENABLED ===
=== SYSTEM INFO ===
Linux xxxx aarch64  # VPS architecture
=== JAVA VERSION ===
OpenJDK Runtime Environment
=== PATH ===
/flyway:/usr/local/sbin:/usr/local/bin...
=== EXECUTABLE TESTS ===
✓ /flyway/flyway exists and executable
```

#### Wrapper Execution:
```
=== WRAPPER EXECUTION ===
Args: migrate
Working directory: /flyway
About to execute: /flyway/flyway migrate
```

#### Error Location:
Nếu vẫn lỗi "exec: migrate: executable file not found", log sẽ show:
- Exactly đâu trong wrapper script bị fail
- Architecture differences giữa local vs VPS  
- Permission issues hoặc missing dependencies

### 4. Possible VPS-Specific Issues:

#### Architecture Mismatch:
- Local: Apple Silicon (aarch64)
- VPS: Có thể x86_64 
- **Solution**: Check architecture và pull correct image

#### Missing Dependencies:
- VPS có thể missing `glibc` hoặc libraries khác
- **Solution**: Add missing deps trong Dockerfile

#### Permission Issues:
- VPS Docker user permissions
- **Solution**: Check user context và file permissions

#### Path/Environment Differences:
- VPS environment variables
- **Solution**: Compare PATH và env trên VPS vs local

### 5. Advanced Debugging Commands:

#### Nếu container start được:
```bash
# Enter container để check manually
docker-compose run migration bash

# Inside container:
ls -la /flyway/
file /flyway/flyway
ldd /flyway/flyway  # Check dependencies
./flyway --version
```

#### Nếu container không start:
```bash
# Check container logs
docker-compose logs migration

# Build và inspect image
docker build -f Dockerfile.migration --build-arg VPS_DEBUG=true .
docker run -it <image_id> bash
```

### 6. Critical Questions để trả lời:

1. **Architecture**: VPS là x86_64 hay aarch64?
2. **OS**: VPS run Ubuntu/CentOS/etc version nào?
3. **Docker Version**: VPS Docker version và engine?
4. **Build Context**: CI/CD build trên VPS hay pull từ registry?

### 7. Expected Debug Output:

Nếu everything OK, expected log:
```
=== VPS DEBUG MODE ENABLED ===
=== FLYWAY DEBUG INFO ===
total 16
drwxr-xr-x 2 root root 4096 configs
-rwxr-xr-x 1 2000 2000 1575 flyway
=== SYSTEM INFO ===
Linux vps-hostname 5.x.x-x aarch64 GNU/Linux
=== JAVA VERSION ===
OpenJDK Runtime Environment (build 17.0.12+7-alpine-r0)
=== PATH ===
/flyway:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
=== EXECUTABLE TESTS ===
✓ /flyway/flyway exists and executable
=== WRAPPER EXECUTION ===
Args: migrate
Working directory: /flyway
About to execute: /flyway/flyway migrate
Flyway Community Edition 11.1.0 by Redgate
```

Nếu fail, sẽ thấy exactly đâu bị problem trong debug output.
