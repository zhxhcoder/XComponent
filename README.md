# XComponent

XComponent 集合多种android自定义控件


# 使用方法

引用    implementation 'com.zhxh:xcomponentlib:3.1'


        <com.zhxh.xcomponentlib.xstickyhorizon.XStickyNavContainer
            android:id="@+id/xStickynavlayout"
            android:layout_width="fill_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:paddingBottom="15dp">

            <android.support.v7.widget.RecyclerView
                android:id="@+id/xRecyclerview"
                android:layout_width="fill_parent"
                android:layout_height="160dp"
                android:layout_gravity="center_vertical"
                android:background="@color/color_white"
                android:overScrollMode="never"
                android:scrollbars="none" />

        </com.zhxh.xcomponentlib.xstickyhorizon.XStickyNavContainer>
        
        
       xStickynavlayout.setOnStartActivity(() -> RequestManager.toQuantTacticsReverse(strategyId, pageTitle));



辅助快速搭建一个具备基本设计还原效果的 Android 项目，同时利用自身提供的丰富控件及兼容处理，让开发者能专注于业务需求而无需耗费精力在基础代码的设计上。
不管是新项目的创建，或是已有项目的维护，均可使开发效率和项目质量得到大幅度提升。

全局 UI 配置
只需要修改一份配置表就可以调整 App 的全局样式，包括组件颜色、导航栏、对话框、列表等。一处修改，全局生效。

丰富的 UI 控件
提供丰富常用的 UI 控件，例如 BottomSheet、Tab、圆角 ImageView、下拉刷新等，使用方便灵活，并且支持自定义控件的样式。

高效的工具方法
提供高效的工具方法，包括设备信息、屏幕信息、键盘管理、状态栏管理等，可以解决各种常见场景并大幅度提升开发效率。

# 部分展示效果

![](https://github.com/zhxhcoder/XComponent/blob/master/screenshots/xcomponent.gif)
