
# XComponent简介
XComponent ([源码地址](https://github.com/zhxhcoder/XComponent))整合了多种android自定义控件，包括但不限于：

- XStickyNavContainer
- TimeTextView
- ExpandableTextView
- SingleFitTextView
- XTabLayout
- SineWaveView
...

本文介绍XStickyNavContainer自定义控件, 该控件实现"右拉查看更多", 释放还原, 等功能.


### 0. 源码地址
https://github.com/zhxhcoder/XComponent

### 1. 引用方法

```
implementation 'com.zhxh:xcomponentlib:3.1'
```

### 2. 使用方法


举个栗子：

```
    <com.zhxh.xcomponentlib.xstickyhorizon.XStickyNavContainer
        android:id="@+id/head_home_layout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toBottomOf="@+id/sineView">

        <android.support.v7.widget.RecyclerView
            android:id="@+id/head_home_recyclerview"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="#ffffff"
            android:overScrollMode="never"
            android:scrollbars="none" />

    </com.zhxh.xcomponentlib.xstickyhorizon.XStickyNavContainer>

```
```
        XStickyNavContainer layout = findViewById(R.id.head_home_layout);

        layout.setOnStartActivity(() -> {
            startActivity(new Intent(MainActivity.this, TabHomeActivity.class));

        });
```
部分效果:
![](https://github.com/zhxhcoder/XComponent/blob/master/screenshots/xcomponent.gif)

### 3. 源码分析

该类继承LinearLayout并实现NestedScrollingParent接口，因为这个容器的子view是一个可以横向滑动的RecycleView所以不可避免的要解决滑动冲突问题。我们一般解决滑动冲突的方法：**外部拦截法**或**内部拦截法**。（在Android开发艺术探索第3章也有详细描述）

**1，外部拦截法：**
即父View根据需要对事件进行拦截。逻辑处理放在父View的onInterceptTouchEvent方法中。我们只需要重写父View的onInterceptTouchEvent方法，并根据逻辑需要做相应的拦截即可。

```
public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercepted = false;
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                intercepted = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (需要拦截) {
                    intercepted = true;
                } else {
                    intercepted = false;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                intercepted = false;
                break;
            }
            default:
                break;
        }
        mLastXIntercept = x;
        mLastYIntercept = y;
        return intercepted;
    }
```
主要事项：
- ACTION_DOWN 一定返回false，不要拦截它，否则根据View事件分发机制，后续ACTION_MOVE 与 ACTION_UP事件都将默认交给父View去处理！
- 原则上ACTION_UP也需要返回false，如果返回true，并且滑动事件交给子View处理，那么子View将接收不到ACTION_UP事件，子View的onClick事件也无法触发。而父View不一样，如果父View在ACTION_MOVE中开始拦截事件，那么后续ACTION_UP也将默认交给父View处理！

**1，内部拦截法：**
即父View不拦截任何事件，所有事件都传递给子View，子View根据需要决定是自己消费事件还是给父View处理。这需要子View使用requestDisallowInterceptTouchEvent方法才能正常工作。下面是子View的dispatchTouchEvent方法的伪代码：

```
public boolean dispatchTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                parent.requestDisallowInterceptTouchEvent(true);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int deltaX = x - mLastX;
                int deltaY = y - mLastY;
                if (父容器需要拦截) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                break;
            }
            default:
                break;
        }

        mLastX = x;
        mLastY = y;
        return super.dispatchTouchEvent(event);
    }

```
父View需要重写onInterceptTouchEvent方法：

```
public boolean onInterceptTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            return false;
        } else {
            return true;
        }
    }

```
注意事项：

- 内部拦截法要求父View不能拦截ACTION_DOWN事件，由于ACTION_DOWN不受FLAG_DISALLOW_INTERCEPT标志位控制，一旦父容器拦截ACTION_DOWN那么所有的事件都不会传递给子View。
- 滑动策略的逻辑放在子View的dispatchTouchEvent方法的ACTION_MOVE中，如果父容器需要获取点击事件则调用 parent.requestDisallowInterceptTouchEvent(false)方法，让父容器去拦截事件


但是还有更简单的方法解决滑动冲突，**NestedScrolling**。
NestedScrolling，在 V4 包下面，在 22.10 版本的时候添加进来，支持 5.0 及 5.0 以上的系统。
在传统的事件分发机制 中，一旦某个 View 或者 ViewGroup 消费了事件，就很难将事件交给父 View 进行共同处理。而 NestedScrolling 机制很好地帮助我们解决了这一问题。我们只需要按照规范实现相应的接口即可，子 View 实现 NestedScrollingChild，父 View 实现 NestedScrollingParent ，通过 NestedScrollingChildHelper 或者 NestedScrollingParentHelper 完成交互。

NestedScrolling机制 能够让 父view 和 子view 在滚动时进行配合，其基本流程如下：

1. 当 子view 开始滚动之前，可以通知 父view，让其先于自己进行滚动;
2. 子view 自己进行滚动
3. 子view 滚动之后，还可以通知 父view 继续滚动

要实现这样的交互，父View 需要实现 NestedScrollingParent接口，而 子View 需要实现NestedScrollingChild接口。

在这套交互机制中，child 是动作的发起者，parent 只是接受回调并作出响应。

另外：父view 和 子view 并不需要是直接的父子关系，即如果 "parent1 包含 parent2，parent2 包含child”，则 parent1 和child 仍能通过 NestedScrolling机制 进行交互

具体代码如下：
```

    /**
     * 返回true代表处理本次事件
     * 在执行动画时间里不能处理本次事件
     */
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return target instanceof RecyclerView && !isRunAnim;
    }
    /**
     * 必须要复写 onStartNestedScroll后调用
     */
    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    /**
     * 复位初始位置
     * scrollTo 移动到指定坐标
     * scrollBy 在原有坐标上面移动
     */
    @Override
    public void onStopNestedScroll(View target) {
        mParentHelper.onStopNestedScroll(target);
        // 如果不在RecyclerView滑动范围内
        if (maxWidth != getScrollX()) {
            startAnimation(new ProgressAnimation());
        }

        if (getScrollX() > maxWidth + maxWidth / 2 && mlistener != null) {
            mlistener.onStart();
        }
    }
    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

    }

    /**
     * @param dx       水平滑动距离
     * @param dy       垂直滑动距离
     * @param consumed 父类消耗掉的距离
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        getParent().requestDisallowInterceptTouchEvent(true);
        // dx>0 往左滑动 dx<0往右滑动
        //System.out.println("dx:" + dx + "=======getScrollX:" + getScrollX() + "==========canScrollHorizontally:" + !ViewCompat.canScrollHorizontally(target, -1));
        boolean hiddenLeft = dx > 0 && getScrollX() < maxWidth && !ViewCompat.canScrollHorizontally(target, -1);
        boolean showLeft = dx < 0 && !ViewCompat.canScrollHorizontally(target, -1);
        boolean hiddenRight = dx < 0 && getScrollX() > maxWidth && !ViewCompat.canScrollHorizontally(target, 1);
        boolean showRight = dx > 0 && !ViewCompat.canScrollHorizontally(target, 1);
        if (hiddenLeft || showLeft || hiddenRight || showRight) {
            scrollBy(dx / DRAG, 0);
            consumed[0] = dx;
        }

        if (hiddenRight || showRight) {
            mFooterView.setRefresh(dx / DRAG);
        }

        // 限制错位问题
        if (dx > 0 && getScrollX() > maxWidth && !ViewCompat.canScrollHorizontally(target, -1)) {
            scrollTo(maxWidth, 0);
        }
        if (dx < 0 && getScrollX() < maxWidth && !ViewCompat.canScrollHorizontally(target, 1)) {
            scrollTo(maxWidth, 0);
        }
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    /**
     * 子view是否可以有惯性 解决右滑时快速左滑显示错位问题
     *
     * @return true不可以  false可以
     */
    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        // 当RecyclerView在界面之内交给它自己惯性滑动
        return getScrollX() != maxWidth;
    }

    @Override
    public int getNestedScrollAxes() {
        return 0;
    }
```
```
public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes)
child:ViewParent包含触发嵌套滚动的view的对象
target:触发嵌套滚动的view (在这里如果不涉及多层嵌套的话,child和target)是相同的
nestedScrollAxes:就是嵌套滚动的滚动方向了.
当子view的调用NestedScrollingChild的方法startNestedScroll时,会调用该方法
该方法决定了当前控件是否能接收到其内部View(并非是直接子View)滑动时的参数
public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes);
如果onStartNestedScroll方法返回true,之后就会调用该方法.它是让嵌套滚动在开始滚动之前,让布局容器(viewGroup)或者它的父类执行一些配置的初始化（React to the successful claiming of a nested scroll operation）
public void onStopNestedScroll(View target)
当子view调用stopNestedScroll时会调用该方法,停止滚动
public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed)
target:同上
dxConsumed:表示target已经消费的x方向的距离
dyConsumed:表示target已经消费的x方向的距离
dxUnconsumed:表示x方向剩下的滑动距离
dyUnconsumed:表示y方向剩下的滑动距离
当子view调用dispatchNestedScroll方法时,会调用该方法
public void onNestedPreScroll(View target, int dx, int dy, int[] consumed)
target:同上
dx:表示target本次滚动产生的x方向的滚动总距离
dy:表示target本次滚动产生的y方向的滚动总距离
consumed:表示父布局要消费的滚动距离,consumed[0]和consumed[1]分别表示父布局在x和y方向上消费的距离.
当子view调用dispatchNestedPreScroll方法是,会调用该方法

```

```
    /**
     * @param dx       水平滑动距离
     * @param dy       垂直滑动距离
     * @param consumed 父类消耗掉的距离
     */
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        getParent().requestDisallowInterceptTouchEvent(true);
        // dx>0 往左滑动 dx<0往右滑动
        //System.out.println("dx:" + dx + "=======getScrollX:" + getScrollX() + "==========canScrollHorizontally:" + !ViewCompat.canScrollHorizontally(target, -1));
        boolean hiddenLeft = dx > 0 && getScrollX() < maxWidth && !ViewCompat.canScrollHorizontally(target, -1);
        boolean showLeft = dx < 0 && !ViewCompat.canScrollHorizontally(target, -1);
        boolean hiddenRight = dx < 0 && getScrollX() > maxWidth && !ViewCompat.canScrollHorizontally(target, 1);
        boolean showRight = dx > 0 && !ViewCompat.canScrollHorizontally(target, 1);
        if (hiddenLeft || showLeft || hiddenRight || showRight) {
            scrollBy(dx / DRAG, 0);
            consumed[0] = dx;
        }

        if (hiddenRight || showRight) {
            mFooterView.setRefresh(dx / DRAG);
        }

        // 限制错位问题
        if (dx > 0 && getScrollX() > maxWidth && !ViewCompat.canScrollHorizontally(target, -1)) {
            scrollTo(maxWidth, 0);
        }
        if (dx < 0 && getScrollX() < maxWidth && !ViewCompat.canScrollHorizontally(target, 1)) {
            scrollTo(maxWidth, 0);
        }
    }
```

回弹动画的实现：
```
    private class ProgressAnimation extends Animation {

        private ProgressAnimation() {
            isRunAnim = true;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            scrollBy((int) ((maxWidth - getScrollX()) * interpolatedTime), 0);
            if (interpolatedTime == 1) {
                isRunAnim = false;
                mFooterView.setRelease();
            }
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            setDuration(300);
            setInterpolator(new AccelerateInterpolator());
        }
    }
```
我们自动为子view RecycleView上加了footerview
```

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOrientation(LinearLayout.HORIZONTAL);

        if (getChildAt(0) instanceof RecyclerView) {
            mChildView = (RecyclerView) getChildAt(0);
            LayoutParams layoutParams = new LayoutParams(maxWidth, LayoutParams.MATCH_PARENT);
            addView(mHeaderView, 0, layoutParams);
            addView(mFooterView, getChildCount(), layoutParams);
            // 左移
            scrollBy(maxWidth, 0);

            mChildView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // 保证动画状态中 子view不能滑动
                    return isRunAnim;
                }
            });
        }
    }
```
  footerview同样是一个自定义view，CYAnimatorView

```
    public void setRefresh(int width) {
        mMove += width;
        if (mMove < 0) {
            mMove = 0;
        } else if (mMove > XStickyNavContainer.maxWidth) {
            mMove = XStickyNavContainer.maxWidth;
        }
        mView.getLayoutParams().width = mMove;
        mView.getLayoutParams().height = LinearLayout.LayoutParams.MATCH_PARENT;

        if (mMove > XStickyNavContainer.maxWidth / 2) {
            animator_text.setText("释放查看更多");
            animator_arrow.setImageResource(R.drawable.tactics_more_right);
        } else {
            animator_text.setText("滑动查看更多");
            animator_arrow.setImageResource(R.drawable.tactics_more_left);
        }
        requestLayout();
    }
```
根据移动的距离，判断footer显示的文字和图标。

