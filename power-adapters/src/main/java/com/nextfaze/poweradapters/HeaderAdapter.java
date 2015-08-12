package com.nextfaze.poweradapters;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.nextfaze.poweradapters.internal.AdapterUtils.layoutInflater;

/**
 * Wraps an existing {@link PowerAdapter} to provide header views above the wrapped adapters items. This class can be
 * subclassed for greater control over the presence of header views.
 */
public abstract class HeaderAdapter extends PowerAdapterWrapper {

    protected HeaderAdapter(@NonNull PowerAdapter adapter) {
        super(adapter);
    }

    @NonNull
    abstract View getHeaderView(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent, int position);

    abstract int getHeaderCount(boolean visibleOnly);

    @Override
    public final int getItemCount() {
        return getHeaderCount(true) + super.getItemCount();
    }

    @Override
    public final long getItemId(int position) {
        if (isHeader(position)) {
            return NO_ID;
        }
        return super.getItemId(position);
    }

    @Override
    public final int getViewTypeCount() {
        return super.getViewTypeCount() + getHeaderCount(false);
    }

    @Override
    public final int getItemViewType(int position) {
        int itemViewType = headerItemViewType(position);
        if (itemViewType != -1) {
            return itemViewType;
        }
        return super.getItemViewType(position);
    }

    @Override
    public final boolean isEnabled(int position) {
        //noinspection SimplifiableIfStatement
        if (isHeader(position)) {
            return false;
        }
        return super.isEnabled(position);
    }

    @NonNull
    @Override
    public final View newView(@NonNull ViewGroup parent, int itemViewType) {
        int headerIndex = itemViewTypeToHeaderIndex(itemViewType);
        if (headerIndex != -1) {
            return getHeaderView(layoutInflater(parent), parent, headerIndex);
        }
        return super.newView(parent, itemViewType);
    }

    @Override
    public final void bindView(@NonNull View view, @NonNull Holder holder) {
        if (!isHeader(holder.getPosition())) {
            super.bindView(view, holder);
        }
    }

    @Override
    protected final int mapPosition(int outerPosition) {
        return outerPosition - getHeaderCount(true);
    }

    private boolean isHeader(int position) {
        return position < getHeaderCount(true);
    }

    private int headerItemViewType(int position) {
        if (!isHeader(position)) {
            return -1;
        }
        return super.getViewTypeCount() + position;
    }

    private int itemViewTypeToHeaderIndex(int itemViewType) {
        int superViewTypeCount = super.getViewTypeCount();
        if (itemViewType < superViewTypeCount) {
            return -1;
        }
        if (itemViewType >= getViewTypeCount()) {
            return -1;
        }
        return superViewTypeCount - itemViewType;
    }

    public enum VisibilityPolicy {
        ALWAYS() {
            @Override
            boolean shouldShow(@NonNull HeaderAdapter adapter) {
                return true;
            }
        },
        HIDE_IF_EMPTY {
            @Override
            boolean shouldShow(@NonNull HeaderAdapter adapter) {
                return adapter.getAdapter().getItemCount() > 0;
            }
        };

        abstract boolean shouldShow(@NonNull HeaderAdapter adapter);
    }

    public static final class Builder {

        @NonNull
        private final PowerAdapter mAdapter;

        @NonNull
        private final ArrayList<Item> mHeaders = new ArrayList<>();

        @NonNull
        private VisibilityPolicy mVisibilityPolicy = VisibilityPolicy.ALWAYS;

        public Builder(@NonNull PowerAdapter adapter) {
            mAdapter = adapter;
        }

        @NonNull
        public Builder headerView(@NonNull View headerView) {
            mHeaders.add(new Item(headerView));
            return this;
        }

        @NonNull
        public Builder headerResource(@LayoutRes int headerResource) {
            mHeaders.add(new Item(headerResource));
            return this;
        }

        @NonNull
        public Builder visibilityPolicy(@NonNull VisibilityPolicy visibilityPolicy) {
            mVisibilityPolicy = visibilityPolicy;
            return this;
        }

        @NonNull
        public HeaderAdapter build() {
            return new Impl(mAdapter, mHeaders, mVisibilityPolicy);
        }
    }

    private static final class Impl extends HeaderAdapter {

        @NonNull
        private final ArrayList<Item> mHeaders = new ArrayList<>();

        @NonNull
        private final VisibilityPolicy mVisibilityPolicy;

        Impl(@NonNull PowerAdapter adapter, @NonNull List<Item> footers, @NonNull VisibilityPolicy visibilityPolicy) {
            super(adapter);
            mVisibilityPolicy = visibilityPolicy;
            mHeaders.addAll(footers);
        }

        @NonNull
        @Override
        protected View getHeaderView(@NonNull LayoutInflater layoutInflater,
                                     @NonNull ViewGroup parent,
                                     int position) {
            return mHeaders.get(position).get(layoutInflater, parent);
        }

        @Override
        protected int getHeaderCount(boolean visibleOnly) {
            if (visibleOnly && !mVisibilityPolicy.shouldShow(this)) {
                return 0;
            }
            return mHeaders.size();
        }
    }
}
