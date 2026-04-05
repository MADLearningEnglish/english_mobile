# Android Custom View - Kiến thức cơ bản

## 1. Các keyword cốt lõi cần nắm

### View Lifecycle & Rendering

| Keyword | Mô tả |
|---------|-------|
| `onMeasure()` | Xác định **kích thước** (width, height) của View. Hệ thống hỏi: "View này cần bao nhiêu không gian?" |
| `onLayout()` | Xác định **vị trí** của các child views. Hệ thống hỏi: "Đặt các child ở đâu?" |
| `onDraw()` | **Vẽ** nội dung lên Canvas (hình, chữ, đường kẻ...). Chỉ dùng khi cần vẽ custom graphics |
| `MeasureSpec` | Object chứa **size + mode** mà parent truyền xuống cho child khi đo kích thước |
| `Canvas` | "Tấm vải" để vẽ lên trong `onDraw()` |
| `Paint` | "Cọ vẽ" - định nghĩa màu sắc, font, style khi vẽ lên Canvas |
| `invalidate()` | Yêu cầu **vẽ lại** View (trigger `onDraw()`) |
| `requestLayout()` | Yêu cầu **đo + layout lại** View (trigger `onMeasure()` → `onLayout()` → `onDraw()`) |

### Custom Attributes

| Keyword | Mô tả |
|---------|-------|
| `declare-styleable` | Khai báo nhóm attribute trong `res/values/attrs.xml` |
| `TypedArray` | Object để đọc giá trị attribute từ XML |
| `obtainStyledAttributes()` | Hàm lấy `TypedArray` từ `AttributeSet` |
| `typedArray.recycle()` | Giải phóng bộ nhớ sau khi đọc xong attribute |
| `format` | Kiểu dữ liệu của attribute: `integer`, `string`, `dimension`, `color`, `boolean`, `reference`, `enum`, `float` |

### View Construction

| Keyword | Mô tả |
|---------|-------|
| `@JvmOverloads` | Annotation Kotlin tự generate các constructor overload cần thiết cho Android View |
| `AttributeSet` | Chứa tất cả attribute được khai báo trong XML |
| `defStyleAttr` | Default style attribute, dùng cho theming |

---

## 2. Flow render UI của Android

```
View được thêm vào layout
        │
        ▼
┌─── MEASURE PASS ───┐
│                     │
│  Parent gọi         │
│  child.measure()    │
│       │             │
│       ▼             │
│  onMeasure()        │  ← View tự tính width/height
│       │             │    dựa trên MeasureSpec từ parent
│       ▼             │
│  setMeasuredDimension() │ ← Lưu kết quả đo
│                     │
└─────────────────────┘
        │
        ▼
┌─── LAYOUT PASS ────┐
│                     │
│  Parent gọi         │
│  child.layout()     │
│       │             │
│       ▼             │
│  onLayout()         │  ← ViewGroup đặt vị trí
│                     │    (left, top, right, bottom)
│                     │    cho từng child
└─────────────────────┘
        │
        ▼
┌──── DRAW PASS ─────┐
│                     │
│  onDraw(canvas)     │  ← Vẽ nội dung lên Canvas
│       │             │
│  dispatchDraw()     │  ← Vẽ children (ViewGroup)
│       │             │
│  onDrawForeground() │  ← Vẽ foreground (scrollbar...)
│                     │
└─────────────────────┘
```

### MeasureSpec - 3 chế độ đo

| Mode | Ý nghĩa | Khi nào |
|------|----------|---------|
| `EXACTLY` | Parent đã quyết định size chính xác | `layout_width="100dp"` hoặc `match_parent` |
| `AT_MOST` | View được phép lấy tối đa bằng size này | `layout_width="wrap_content"` |
| `UNSPECIFIED` | Không giới hạn, View muốn bao nhiêu cũng được | Bên trong `ScrollView` |

---

## 3. Các bước tạo Custom View

### Bước 1: Khai báo custom attributes (`res/values/attrs.xml`)

```xml
<resources>
    <declare-styleable name="TrackingButton">
        <attr name="trackingEventName" format="string" />
        <attr name="trackingEnabled" format="boolean" />
        <attr name="cornerRadius" format="dimension" />
        <attr name="buttonStyle" format="enum">
            <enum name="primary" value="0" />
            <enum name="secondary" value="1" />
        </attr>
    </declare-styleable>
</resources>
```

### Bước 2: Tạo class kế thừa View/ViewGroup có sẵn

```kotlin
class TrackingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialButton(context, attrs, defStyleAttr) {

    private var trackingEventName: String? = null
    private var trackingEnabled: Boolean = true

    init {
        // Đọc attribute từ XML
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TrackingButton)
        trackingEventName = typedArray.getString(R.styleable.TrackingButton_trackingEventName)
        trackingEnabled = typedArray.getBoolean(R.styleable.TrackingButton_trackingEnabled, true)
        typedArray.recycle()
    }
}
```

### Bước 3: Dùng trong XML

```xml
<com.example.view.TrackingButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:text="Add to Cart"
    app:trackingEventName="add_to_cart_clicked"
    app:trackingEnabled="true"
    app:cornerRadius="8dp"
    app:buttonStyle="primary" />
```

### Bước 4 (tuỳ chọn): Override lifecycle methods

```kotlin
// Override onMeasure nếu cần thay đổi cách đo kích thước
override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) { ... }

// Override onDraw nếu cần vẽ custom graphics
override fun onDraw(canvas: Canvas) { ... }

// Override onLayout nếu tạo custom ViewGroup
override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) { ... }
```

---

## 4. Tích hợp Click Tracking + API Call

Hoàn toàn được. Ví dụ tạo một View mà khi click sẽ gọi API tracking:

```kotlin
class TrackingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : MaterialButton(context, attrs, defStyleAttr) {

    private var trackingEventName: String? = null
    private var trackingEnabled: Boolean = true

    // Callback để Fragment/Activity inject logic gọi API
    var onTrackingClick: ((eventName: String) -> Unit)? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TrackingButton)
        trackingEventName = typedArray.getString(R.styleable.TrackingButton_trackingEventName)
        trackingEnabled = typedArray.getBoolean(R.styleable.TrackingButton_trackingEnabled, true)
        typedArray.recycle()

        super.setOnClickListener { view ->
            if (trackingEnabled && trackingEventName != null) {
                onTrackingClick?.invoke(trackingEventName!!)
            }
        }
    }
}
```

Sử dụng trong Fragment:

```kotlin
binding.btnAddToCart.onTrackingClick = { eventName ->
    // Gọi API tracking qua ViewModel
    viewModel.trackEvent(eventName)
}
```

### Kiến trúc khuyến nghị

```
XML (config tĩnh: eventName, enabled)
  │
  ▼
Custom View (đọc config, bắt sự kiện click)
  │
  ▼
Fragment/Activity (inject callback)
  │
  ▼
ViewModel (xử lý business logic)
  │
  ▼
Repository → API Service (gọi tracking API)
```

> **Lưu ý:** View KHÔNG nên gọi API trực tiếp. View chỉ nên phát sự kiện,
> để Fragment/ViewModel xử lý logic. Điều này giữ đúng nguyên tắc tách biệt (Separation of Concerns).

---

## 5. Các Custom View thường gặp trong dự án thực tế

### UI Components

| Custom View | Mục đích | Override chính |
|-------------|----------|----------------|
| **LoadingButton** | Button có progress indicator khi đang loading | `onDraw()`, custom attrs |
| **MaxHeightScrollView** | ScrollView giới hạn chiều cao tối đa | `onMeasure()` |
| **RatioImageView** | ImageView giữ tỷ lệ width:height cố định | `onMeasure()` |
| **CircularProgressView** | Vẽ vòng tròn progress tuỳ chỉnh | `onDraw()` |
| **PinCodeInput** | Input OTP/PIN code nhiều ô | `onDraw()`, `onKeyEvent()` |
| **BadgeView** | View hiển thị badge count (notification) | `onDraw()` |
| **CollapsibleTextView** | TextView "Xem thêm / Thu gọn" | `onMeasure()`, click handler |
| **SwipeRevealLayout** | Vuốt để hiển thị action (xoá, sửa) | `onTouchEvent()`, `onLayout()` |

### Layout / Container

| Custom View | Mục đích | Override chính |
|-------------|----------|----------------|
| **FlowLayout** | Tự động xuống dòng khi hết chỗ (tag chips) | `onMeasure()`, `onLayout()` |
| **MaxItemRecyclerView** | RecyclerView giới hạn số item hiển thị | `onMeasure()` |
| **KeyboardAwareLayout** | Layout tự điều chỉnh khi bàn phím mở | `onMeasure()`, `WindowInsets` |

### Analytics / Tracking

| Custom View | Mục đích | Override chính |
|-------------|----------|----------------|
| **TrackingButton** | Button tự gửi tracking event khi click | Click listener, custom attrs |
| **ImpressionTrackingView** | Tự tracking khi View xuất hiện trên màn hình | `onAttachedToWindow()`, `OnScrollListener` |
| **TrackableRecyclerView** | Tracking item nào user đã xem | `OnScrollListener`, `onChildAttachedToWindow()` |

---

## 6. Tổng kết - Khi nào cần Custom View?

| Tình huống | Giải pháp |
|------------|-----------|
| Chỉ cần style khác (màu, font, padding) | Dùng **style/theme** trong XML, không cần custom view |
| Cần thêm attribute để config từ XML | **Custom View** + `declare-styleable` |
| Cần thay đổi cách đo kích thước | Override **`onMeasure()`** |
| Cần vẽ đồ hoạ tuỳ chỉnh (chart, shape) | Override **`onDraw()`** |
| Cần layout con theo cách riêng | Override **`onLayout()`** (custom ViewGroup) |
| Cần xử lý gesture phức tạp | Override **`onTouchEvent()`** + `GestureDetector` |
| Cần kết hợp nhiều View thành 1 component | **Compound View** (inflate layout trong init) |
