package com.example.repository;

import java.io.Serializable;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.example.model.EntityBase;
import com.example.model.TreeNode;

@NoRepositoryBean
public interface ITreeNodeRepository<T extends TreeNode<T>, ID extends Serializable>
		extends JpaRepository<T, ID> /* extends IRepository<T> */ {
	boolean support(String modelType);

	/**
	 *	根据树结构的Id，获取节点及其子节点下的所有数据（包括子节点下的子节点）<br/>
	 *		使用Lazyloading的四种方法: <br/>
	 *		https://thoughts-on-java.org/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
	 *@param clazz 泛型T的Class类型
	 *@param name 树结构的Id
	 *@return T 返回结果，形如：<br/>
     * --node  --> Id == id	<br/>
     *		--node			<br/>
     *			--node		<br/>
     *			--node		<br/>
     *				--node	<br/>
     *		--node			<br/>
     *			--node		<br/>
	 */
	@EntityGraph(value = "TreeNode.Graph", type = EntityGraph.EntityGraphType.FETCH)
	T getTreeNodeWithNestChildById(Class<T> clazz, int id);

	/**
	 *	根据树结构的Name，获取节点及其子节点下的所有数据（包括子节点下的子节点）<br/>
	 *		使用Lazyloading的四种方法: <br/>
	 *		https://thoughts-on-java.org/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
	 *@param clazz 泛型T的Class类型
	 *@param name 树结构的Name
	 *@return T 返回结果，形如：<br/>
     * --node  --> Id == id	<br/>
     *		--node			<br/>
     *			--node		<br/>
     *			--node		<br/>
     *				--node	<br/>
     *		--node			<br/>
     *			--node		<br/>
	 */
	//@EntityGraph(value = "TreeNode.Graph", type = EntityGraph.EntityGraphType.FETCH)
	List<T> findAllTreeNodeWithNestChildByName(Class<T> clazz, String name);

	/**
	 *    根据树结构的TreeCode，获取包含其父节点（包含父节点上的父节点）及其子节点（子节点的子节点）<br/>
	 *		使用Lazyloading的四种方法: <br/>
	 *		https://thoughts-on-java.org/5-ways-to-initialize-lazy-relations-and-when-to-use-them/
	 *		https://www.baeldung.com/spring-data-jpa-named-entity-graphs
	 *@param clazz 泛型T的Class类型
	 *@param name 树结构的Name
	 *@return T 返回结果，形如：	<br/>
     * --node  					<br/>
     *     --node				<br/>
     *         --node --> Id == id （TreeCode：1-1001-10001）<br/>
     *             --node		<br/>
     *             --node		<br/>
	 */
	// @EntityGraph(value = "TreeNode.Graph", type = EntityGraph.EntityGraphType.FETCH)
	List<T> findTreeNodesWithNestParentAndChildByTreeCode(Class<T> clazz, String treeCode);
	
	
	/**
	 * 根据树节点Id，删除树节点及其下面的子节点
	 *@param clazz 泛型T的Class类型
	 *@param id 树结构的id
	 *@return boolean
	 */
	boolean removeTreeNodeWithChildById(Class<T> clazz, int id);
	/**
	 * 删除树节点列表及其下面的子节点
	 *@param nodes 树节点列表
	 *@return int 影响行数
	 */
	int removeTreeNodesWithChild(Iterable<T> nodes);
	/**
	 * 删除树节点及其下面的子节点
	 *@param clazz 泛型T的Class类型
	 *@param id 树结构的id
	 *@return boolean
	 */
	boolean removeTreeNodeWithChild(T node);
	
	/**
	 *    保持树结构类别，同时更新树的扩展属性：TreeCode、Level、Leaf
	 *@param entities 需要保持树结构列表
	 *@return List<T> 返回树结构列表
	 */
	List<T> saveAllTreeNodeWithExtensionFields(Iterable<T> entities);
	
	boolean executeUpdateSql(String query, Object... parameters);

	<K extends EntityBase> List<K> sqlQuery(String selectSql);

}