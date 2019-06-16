package com.example.repository;

import java.io.Serializable;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.function.Function;

import com.example.model.EntityBase;

@NoRepositoryBean
public interface IDbRepository<T extends EntityBase, ID extends Serializable>
		extends JpaRepository<T, ID> /* extends IRepository<T> */ {

	boolean support(String modelType);

	/*
	 * int Add(List<T> entities, boolean isSave);
	 * 
	 * boolean ExistByFilter(Predicate<T> predicate);
	 * 
	 * T FindById(Object... keyValues); T FindByFilter(Predicate<T> predicate);
	 * 
	 * <K extends EntityBase> List<T> FindPagenatedList(int pageIndex, int pageSize,
	 * Predicate<T> predicate, Function<T, K> keySelector, boolean ascending);
	 * 
	 * <K extends EntityBase> TupleTwo<Integer, List<T>>
	 * FindPagenatedListWithCount(int pageIndex, int pageSize, Predicate<T>
	 * predicate, Function<T, K> keySelector, boolean ascending);
	 * 
	 * boolean Modify(T entity, String[] properties, boolean isSave); int
	 * Modify(List<T> entities, String[] properties, boolean isSave);
	 * 
	 * int Remove(Predicate<T> predicate, boolean isSave);
	 * 
	 * boolean SoftRemove(Object id, boolean isSave); boolean SoftRemove(T entity,
	 * boolean isSave); int SoftRemove(Predicate<T> predicate, boolean isSave); int
	 * SoftRemove(List<T> entities, boolean isSave);
	 * 
	 * <K extends TreeNode<K>> K GetTreeNodeWithNestChildById(int id);
	 * 
	 * <K extends TreeNode<K>> List<T> FindPagenatedTreeList(int pageIndex, int
	 * pageSize, Function<T, List<List<List<T>>>> including, Predicate<K> predicate,
	 * Function<T, K> keySelector, boolean ascending); <K extends TreeNode<K>>
	 * TupleTwo<Integer, List<T>> FindPagenatedTreeListWithCount(int pageIndex, int
	 * pageSize, Function<T, List<List<List<T>>>> including, Predicate<K> predicate,
	 * Function<T, K> keySelector, boolean ascending);
	 * 
	 * <K extends TreeNode<K>> List<K> GetAllTreeNodeWithNestChild(); <K extends
	 * TreeNode<K>> List<K> GetAllTreeNodeWithNestChildByFilter(Predicate<K>
	 * predicate);
	 * 
	 * <K extends TreeNode<K>> List<K>
	 * GetNodeListContainNestChildByFilter(Predicate<K> predicate); <K extends
	 * TreeNode<K>> List<K> GetNodeListContainNestChildByParentId(Integer parentId);
	 * 
	 * <K extends TreeNode<K>> List<K>
	 * GetTreeNodesWithNestParentAndChildByFilter(Predicate<K> predicate); <K
	 * extends TreeNode<K>> List<K> GetTreeNodesWithNestParentAndChildById(int id);
	 * <K extends TreeNode<K>> List<K>
	 * GetTreeNodesWithNestParentAndChildByTreeCode(String treeCode);
	 * 
	 * <K extends TreeNode<K>> boolean UpdateExtendFields(); <K extends TreeNode<K>>
	 * boolean UpdateExtendFieldsByFilter(Predicate<K> predicate);
	 */

	boolean executeUpdateSql(String updateSql, Object... parameters);

	 <K extends EntityBase> List<K> sqlQuery(String selectSql); 

}