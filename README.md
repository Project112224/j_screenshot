# j_screenshot

該插件是截圖監聽用

## Android
目前使用監聽 Storage 觸發，因此需開啟圖片權限，若使用者不同意則將截圖圖片變黑
當監聽到圖片時預設回傳字串為圖片名稱

## iOS
使用 UIApplication.userDidTakeScreenshotNotification
此做法不需要權限，但適當監聽到截圖也不會回傳圖片名稱