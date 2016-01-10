package com.nextfaze.asyncdata;

import lombok.NonNull;

@SuppressWarnings("Convert2MethodRef")
public abstract class DataWrapper<T> extends AbstractData<T> {

    @NonNull
    private final Data<?> mData;

    @NonNull
    private final DataObserver mDataObserver = new SimpleDataObserver() {
        @Override
        public void onChange() {
            notifyDataChanged();
        }
    };

    @NonNull
    private final LoadingObserver mLoadingObserver = new LoadingObserver() {
        @Override
        public void onLoadingChange() {
            notifyLoadingChanged();
        }
    };

    @NonNull
    private final ErrorObserver mErrorObserver = new ErrorObserver() {
        @Override
        public void onError(@NonNull Throwable e) {
            notifyError(e);
        }
    };

    @NonNull
    private final AvailableObserver mAvailableObserver = new AvailableObserver() {
        @Override
        public void onAvailableChange() {
            notifyAvailableChanged();
        }
    };

    private final boolean mTakeOwnership;

    private boolean mObservingData;
    private boolean mObservingLoading;
    private boolean mObservingError;
    private boolean mObservingAvailable;

    public DataWrapper(@NonNull Data<?> data) {
        this(data, true);
    }

    public DataWrapper(@NonNull Data<?> data, boolean takeOwnership) {
        mData = data;
        mTakeOwnership = takeOwnership;
    }

    @Override
    public void close() {
        unregisterAll();
        if (mTakeOwnership) {
            mData.close();
        }
        super.close();
    }

    @Override
    public void invalidate() {
        mData.invalidate();
    }

    @Override
    public void refresh() {
        mData.reload();
    }

    @Override
    public void reload() {
        mData.reload();
    }

    @Override
    public int size() {
        return mData.size();
    }

    @Override
    public boolean isLoading() {
        return mData.isLoading();
    }

    @Override
    public int available() {
        return mData.available();
    }

    @Override
    public boolean isEmpty() {
        return mData.isEmpty();
    }

    @Override
    public void registerDataObserver(@NonNull DataObserver dataObserver) {
        super.registerDataObserver(dataObserver);
        updateDataObserver();
    }

    @Override
    public void unregisterDataObserver(@NonNull DataObserver dataObserver) {
        super.unregisterDataObserver(dataObserver);
        updateDataObserver();
    }

    @Override
    public void registerAvailableObserver(@NonNull AvailableObserver availableObserver) {
        super.registerAvailableObserver(availableObserver);
        updateAvailableObserver();
    }

    @Override
    public void unregisterAvailableObserver(@NonNull AvailableObserver availableObserver) {
        super.unregisterAvailableObserver(availableObserver);
        updateAvailableObserver();
    }

    @Override
    public void registerLoadingObserver(@NonNull LoadingObserver loadingObserver) {
        super.registerLoadingObserver(loadingObserver);
        updateLoadingObserver();
    }

    @Override
    public void unregisterLoadingObserver(@NonNull LoadingObserver loadingObserver) {
        super.unregisterLoadingObserver(loadingObserver);
        updateLoadingObserver();
    }

    @Override
    public void registerErrorObserver(@NonNull ErrorObserver errorObserver) {
        super.registerErrorObserver(errorObserver);
        updateErrorObserver();
    }

    @Override
    public void unregisterErrorObserver(@NonNull ErrorObserver errorObserver) {
        super.unregisterErrorObserver(errorObserver);
        updateErrorObserver();
    }

    private void updateDataObserver() {
        if (mObservingData && getDataObserverCount() <= 0) {
            mData.unregisterDataObserver(mDataObserver);
            mObservingData = false;
        } else if (!mObservingData && getDataObserverCount() > 0) {
            mData.registerDataObserver(mDataObserver);
            mObservingData = true;
        }
    }

    private void updateLoadingObserver() {
        if (mObservingLoading && getLoadingObserverCount() <= 0) {
            mData.unregisterLoadingObserver(mLoadingObserver);
            mObservingLoading = false;
        } else if (!mObservingLoading && getLoadingObserverCount() > 0) {
            mData.registerLoadingObserver(mLoadingObserver);
            mObservingLoading = true;
        }
    }

    private void updateAvailableObserver() {
        if (mObservingAvailable && getAvailableObserverCount() <= 0) {
            mData.unregisterAvailableObserver(mAvailableObserver);
            mObservingAvailable = false;
        } else if (!mObservingAvailable && getAvailableObserverCount() > 0) {
            mData.registerAvailableObserver(mAvailableObserver);
            mObservingAvailable = true;
        }
    }

    private void updateErrorObserver() {
        if (mObservingError && getErrorObserverCount() <= 0) {
            mData.unregisterErrorObserver(mErrorObserver);
            mObservingError = false;
        } else if (!mObservingError && getErrorObserverCount() > 0) {
            mData.registerErrorObserver(mErrorObserver);
            mObservingError = true;
        }
    }

    private void unregisterAll() {
        if (mObservingData) {
            mData.unregisterDataObserver(mDataObserver);
            mObservingData = false;
        }
        if (mObservingLoading) {
            mData.unregisterLoadingObserver(mLoadingObserver);
            mObservingLoading = false;
        }
        if (mObservingAvailable) {
            mData.unregisterAvailableObserver(mAvailableObserver);
            mObservingAvailable = false;
        }
        if (mObservingError) {
            mData.unregisterErrorObserver(mErrorObserver);
            mObservingError = false;
        }
    }
}

