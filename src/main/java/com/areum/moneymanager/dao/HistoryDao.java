package com.areum.moneymanager.dao;


public interface HistoryDao<T, PK> {

	T saveHistory(T t);

	T findHistory( PK pk );
}
