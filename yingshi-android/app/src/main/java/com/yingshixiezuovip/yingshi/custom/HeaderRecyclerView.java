package com.yingshixiezuovip.yingshi.custom;

import android.content.Context;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yingshixiezuovip.yingshi.adapter.PublishNewAdapter;
import com.yingshixiezuovip.yingshi.publish.CallbackItemTouch;

/**
 * Created by Resmic on 18/1/31.
 * Email:xiangyx@wenwen-tech.com
 * <p>
 * <p>
 * describe：
 */

@SuppressWarnings("ALL")
public class HeaderRecyclerView extends RecyclerView {
    private RecyclerView.Adapter mAdapter;
    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFooterViews = new SparseArrayCompat<>();
    private View mLoaderView;
    private int mCounts;

    public HeaderRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderRecyclerView(Context context) {
        super(context);
    }

    public HeaderRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addHeaderView(View v) {
        mHeaderViews.put(HeaderViewRecyclerAdapter.VIEW_TYPE_HEADER + mCounts++, v);
        // Wrap the adapter if it wasn't already wrapped.
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderViewRecyclerAdapter)) {
                wrapHeaderViewRecyclerAdapter();
            }
            // In the case of re-adding a header view, or adding one later on,
            // we need to notify the observer.
            mAdapter.notifyItemInserted(mHeaderViews.size() - 1);
        }
    }

    private void wrapHeaderViewRecyclerAdapter() {
        mAdapter = wrapHeaderViewRecyclerAdapter(mHeaderViews, mFooterViews, mLoaderView, mAdapter);
    }

    private Adapter wrapHeaderViewRecyclerAdapter(SparseArrayCompat<View> headerViews, SparseArrayCompat<View> footerViews, View loaderView, Adapter adapter) {
        return new HeaderViewRecyclerAdapter(headerViews, footerViews, loaderView, adapter);
    }

    public int getHeaderViewsCount() {
        return mHeaderViews.size();
    }

    public boolean removeHeaderView(View v) {
        if (mHeaderViews.size() > 0) {
            boolean result = false;
            if (mAdapter != null && ((HeaderViewRecyclerAdapter) mAdapter).removeHeader(v)) {
                result = true;
            }
            removeFixedView(v, mHeaderViews);
            return result;
        }
        return false;
    }

    private void removeFixedView(View v, SparseArrayCompat<View> where) {
        int index = where.indexOfValue(v);
        if (index >= 0)
            where.removeAt(index);

    }

    public void addFooterView(View v) {
        mFooterViews.put(HeaderViewRecyclerAdapter.VIEW_TYPE_FOOTER + mCounts++, v);
        // Wrap the adapter if it wasn't already wrapped.
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderViewRecyclerAdapter)) {
                wrapHeaderViewRecyclerAdapter();
            }
            // In the case of re-adding a header view, or adding one later on,
            // we need to notify the observer.
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1 - getLoaderViewsCount());
        }
    }

    public int getFooterViewsCount() {
        return mFooterViews.size();
    }

    public boolean removeFooterView(View v) {
        if (mFooterViews.size() > 0) {
            boolean result = false;
            if (mAdapter != null && ((HeaderViewRecyclerAdapter) mAdapter).removeFooter(v)) {
                result = true;
            }
            removeFixedView(v, mFooterViews);
            return result;
        }
        return false;
    }

    public void addLoaderView(View v) {
        mLoaderView = v;
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderViewRecyclerAdapter)) {
                wrapHeaderViewRecyclerAdapter();
            }
            // In the case of re-adding a header view, or adding one later on,
            // we need to notify the observer.
            mAdapter.notifyItemInserted(mAdapter.getItemCount() - 1);
        }
    }

    public int getLoaderViewsCount() {
        return mLoaderView != null ? 1 : 0;
    }

    public boolean removeLoaderView() {
        if (mLoaderView != null) {
            boolean result = false;
            if (mAdapter != null && ((HeaderViewRecyclerAdapter) mAdapter).removeLoader()) {
                result = true;
            }
            mLoaderView = null;
            return result;
        }
        return false;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeaderViews.size() > 0 || mFooterViews.size() > 0 || mLoaderView != null) {
            mAdapter = wrapHeaderViewRecyclerAdapter(mHeaderViews, mFooterViews, mLoaderView, adapter);
        } else {
            mAdapter = adapter;
        }
        super.setAdapter(mAdapter);
    }

    public class HeaderViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements CallbackItemTouch {
        public static final int VIEW_TYPE_HEADER = Integer.MAX_VALUE >> 1;
        public static final int VIEW_TYPE_FOOTER = Integer.MAX_VALUE >> 2;
        public static final int VIEW_TYPE_LOADER = Integer.MAX_VALUE;
        private SparseArrayCompat<View> mHeaderViews;
        private SparseArrayCompat<View> mFooterViews;
        private View mLoaderView;
        private RecyclerView.Adapter mAdapter;

        HeaderViewRecyclerAdapter(SparseArrayCompat<View> mHeaderViews, SparseArrayCompat<View> mFooterViews, View mLoaderView,
                                  RecyclerView.Adapter adapter) {
            this.mHeaderViews = mHeaderViews;
            this.mFooterViews = mFooterViews;
            this.mLoaderView = mLoaderView;
            if (mAdapter != null) {
                mAdapter.unregisterAdapterDataObserver(mObserver);
            }
            mAdapter = adapter;
            if (mAdapter != null) {
                mAdapter.registerAdapterDataObserver(mObserver);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if (mHeaderViews.get(viewType) != null) {
                return new ViewHolder(mHeaderViews.get(viewType));
            } else if (mFooterViews.get(viewType) != null) {
                return new ViewHolder(mFooterViews.get(viewType));
            } else if (viewType == VIEW_TYPE_LOADER) {
                return new ViewHolder(mLoaderView);
            }
            if (mAdapter != null)
                return mAdapter.createViewHolder(viewGroup, viewType);
            throw new RuntimeException("position should match header or footer or loader");
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return;
            }
            // Adapter
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    mAdapter.onBindViewHolder(holder, adjPosition);
                }
            }
        }

        @Override
        public int getItemCount() {
            return getFootersCount() + getHeadersCount() + getLoadersCount() + getCount();
        }

        private int getCount() {
            return mAdapter != null ? mAdapter.getItemCount() : 0;
        }

        @Override
        public int getItemViewType(int position) {
            int numHeaders = getHeadersCount();
            if (position < numHeaders) {
                return mHeaderViews.keyAt(position);
            }
            // Adapter
            final int adjPosition = position - numHeaders;
            int adapterCount = 0;
            if (mAdapter != null) {
                adapterCount = mAdapter.getItemCount();
                if (adjPosition < adapterCount) {
                    return mAdapter.getItemViewType(adjPosition);
                }
            }
            int numFooters = getFootersCount();
            final int footPosition = position - numHeaders - adapterCount;
            if (footPosition < numFooters) {
                return mFooterViews.keyAt(footPosition);
            }
            return VIEW_TYPE_LOADER;
        }

        private int getHeadersCount() {
            return mHeaderViews.size();
        }

        private int getFootersCount() {
            return mFooterViews.size();
        }

        private int getLoadersCount() {
            return mLoaderView != null ? 1 : 0;
        }

        private boolean removeHeader(View v) {
            int index = mHeaderViews.indexOfValue(v);
            if (index >= 0) {
                mHeaderViews.removeAt(index);
                notifyItemRemoved(index);
                return true;
            }
            return false;
        }

        private boolean removeFooter(View v) {
            int index = mFooterViews.indexOfValue(v);
            if (index >= 0) {
                mFooterViews.removeAt(index);
                notifyItemRemoved(getHeadersCount() + getCount() + index);
                return true;
            }
            return false;
        }

        private boolean removeLoader() {
            boolean success = mLoaderView != null;
            if (success) {
                mLoaderView = null;
                notifyItemRemoved(getItemCount());
            }
            return success;
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            if (holder instanceof ViewHolder) return;
            if (mAdapter != null) mAdapter.onViewAttachedToWindow(holder);
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
            if (holder instanceof ViewHolder) return;
            if (mAdapter != null) mAdapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            if (holder instanceof ViewHolder) return;
            if (mAdapter != null) mAdapter.onViewRecycled(holder);
        }

        private RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                notifyItemRangeChanged(positionStart + getHeadersCount(), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                notifyItemRangeInserted(positionStart + getHeadersCount(), itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                int headerViewsCountCount = getHeadersCount();
                notifyItemRangeChanged(fromPosition + headerViewsCountCount,
                        toPosition + headerViewsCountCount + itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                notifyItemRangeRemoved(positionStart + getHeadersCount(), itemCount);
            }
        };

        @Override
        public void itemTouchOnMove(int oldPosition, int newPosition) {
            ((PublishNewAdapter) mAdapter).itemTouchOnMove(oldPosition - getHeadersCount(), newPosition - getHeadersCount());
            notifyItemMoved(oldPosition, newPosition); //notifies changes in adapter
        }

        @Override
        public void itemTouchOnFinish() {
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
