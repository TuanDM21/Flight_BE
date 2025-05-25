# Pre-signed URL File Upload System

## Tổng quan

Hệ thống đã được cập nhật để hỗ trợ **pre-signed URL** cho Azure Blob Storage, cho phép client upload file trực tiếp lên Azure mà không cần đi qua server, giảm tải cho server và tăng hiệu suất.

## Kiến trúc mới

### Workflow Pre-signed URL Upload:
```
1. Client → Server: Request pre-signed URL
2. Server → Azure: Tạo SAS token 
3. Server → Client: Trả về pre-signed URL + metadata
4. Client → Azure: Upload file trực tiếp
5. Client → Server: Confirm upload thành công
6. Server: Verify và cập nhật database
```

### So sánh với cách cũ:
```
Cách cũ: Client → Server → Azure (file đi qua server)
Cách mới: Client → Server (chỉ metadata) & Client → Azure (file upload trực tiếp)
```

## API Endpoints

### 1. Tạo Pre-signed URL cho Upload
```http
POST /api/attachments/generate-upload-url
Content-Type: application/json

{
    "fileName": "document.pdf",
    "fileSize": 1048576,
    "contentType": "application/pdf"
}
```

**Response:**
```json
{
    "message": "Tạo pre-signed URL thành công",
    "status": 200,
    "success": true,
    "data": {
        "uploadUrl": "https://storage.blob.core.windows.net/container/uuid_timestamp_document.pdf?sv=2021-06-08&se=2024...",
        "attachmentId": 123,
        "fileName": "document.pdf",
        "uniqueFileName": "uuid_timestamp_document.pdf",
        "expiryTime": "2024-01-15 10:30:00",
        "fileUrl": "https://storage.blob.core.windows.net/container/uuid_timestamp_document.pdf",
        "instructions": "Sử dụng uploadUrl để upload file trực tiếp lên Azure Blob..."
    }
}
```

### 2. Upload File qua Pre-signed URL
```javascript
// Client sử dụng uploadUrl để upload trực tiếp
const formData = new FormData();
formData.append('file', selectedFile);

fetch(uploadUrl, {
    method: 'PUT',
    body: selectedFile,
    headers: {
        'Content-Type': contentType,
        'x-ms-blob-type': 'BlockBlob'
    }
});
```

### 3. Xác nhận Upload thành công
```http
POST /api/attachments/confirm-upload/{attachmentId}
```

**Response:**
```json
{
    "message": "Xác nhận upload thành công",
    "status": 200,
    "success": true,
    "data": {
        "id": 123,
        "fileName": "document.pdf",
        "filePath": "https://storage.blob.core.windows.net/container/uuid_timestamp_document.pdf",
        "fileSize": 1048576,
        "createdAt": "2024-01-15 10:15:00"
    }
}
```

### 4. Tạo Pre-signed URL cho Download
```http
GET /api/attachments/download-url/{attachmentId}
```

**Response:**
```json
{
    "message": "Tạo download URL thành công", 
    "status": 200,
    "success": true,
    "downloadUrl": "https://storage.blob.core.windows.net/container/file.pdf?sv=2021-06-08&se=2024..."
}
```

## Frontend Implementation Example

### React/JavaScript Example:
```javascript
class FileUploadService {
    // Bước 1: Lấy pre-signed URL
    async getUploadUrl(file) {
        const response = await fetch('/api/attachments/generate-upload-url', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                fileName: file.name,
                fileSize: file.size,
                contentType: file.type
            })
        });
        return response.json();
    }
    
    // Bước 2: Upload file trực tiếp lên Azure
    async uploadToAzure(uploadUrl, file) {
        const response = await fetch(uploadUrl, {
            method: 'PUT',
            body: file,
            headers: {
                'Content-Type': file.type,
                'x-ms-blob-type': 'BlockBlob'
            }
        });
        return response.ok;
    }
    
    // Bước 3: Xác nhận upload
    async confirmUpload(attachmentId) {
        const response = await fetch(`/api/attachments/confirm-upload/${attachmentId}`, {
            method: 'POST'
        });
        return response.json();
    }
    
    // Workflow hoàn chỉnh
    async uploadFile(file) {
        try {
            // 1. Lấy pre-signed URL
            const urlResponse = await this.getUploadUrl(file);
            if (!urlResponse.success) throw new Error(urlResponse.message);
            
            const { uploadUrl, attachmentId } = urlResponse.data;
            
            // 2. Upload trực tiếp lên Azure
            const uploadSuccess = await this.uploadToAzure(uploadUrl, file);
            if (!uploadSuccess) throw new Error('Upload failed');
            
            // 3. Xác nhận với server
            const confirmResponse = await this.confirmUpload(attachmentId);
            if (!confirmResponse.success) throw new Error(confirmResponse.message);
            
            return confirmResponse.data;
            
        } catch (error) {
            console.error('Upload error:', error);
            throw error;
        }
    }
}
```

## Ưu điểm của Pre-signed URL

1. **Hiệu suất**: File upload trực tiếp lên Azure, không qua server
2. **Bảo mật**: SAS token có thời hạn (15 phút cho upload, 1 giờ cho download)
3. **Scalability**: Server không bị bottleneck bởi file transfer
4. **Bandwidth**: Tiết kiệm bandwidth của server
5. **User Experience**: Upload nhanh hơn, progress tracking trực tiếp

## Backward Compatibility

Hệ thống vẫn hỗ trợ cách upload cũ:
- `POST /api/attachments/upload-multi` - Upload truyền thống qua server
- Tất cả endpoint GET, PUT, DELETE vẫn hoạt động bình thường

## Configuration

Đảm bảo Azure Storage Account có:
- SAS token permissions được cấu hình đúng
- CORS settings cho phép client access trực tiếp
- Container permissions phù hợp

## Error Handling

- Pre-signed URL có thời hạn 15 phút
- Nếu upload thất bại, record trong database sẽ bị xóa khi confirm
- Client cần handle timeout và retry logic
- Server validate file existence trước khi confirm