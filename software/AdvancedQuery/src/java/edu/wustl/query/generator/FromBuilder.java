package edu.wustl.query.generator;

import static edu.wustl.query.generator.SqlKeyWords.FROM;
import static edu.wustl.query.generator.SqlKeyWords.INNER_JOIN;
import static edu.wustl.query.generator.SqlKeyWords.LEFT_JOIN;
import static edu.wustl.query.generator.SqlKeyWords.ON;
import static edu.wustl.query.generator.SqlKeyWords.SELECT;
import static edu.wustl.query.generator.SqlKeyWords.WHERE;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintKeyPropertiesInterface;
import edu.common.dynamicextensions.domaininterface.databaseproperties.ConstraintPropertiesInterface;
import edu.common.dynamicextensions.util.global.DEConstants.InheritanceStrategy;
import edu.wustl.common.query.queryobject.util.InheritanceUtils;
import edu.wustl.common.querysuite.exceptions.MultipleRootsException;
import edu.wustl.common.querysuite.metadata.associations.IIntraModelAssociation;
import edu.wustl.common.querysuite.queryobject.IExpression;
import edu.wustl.common.querysuite.queryobject.IJoinGraph;
import edu.wustl.common.util.Utility;

/**
 * Note to human debugger: If an error occurs similar to "column foo_bar is
 * ambiguous" while firing a generated SQL, this is because the column "foo_bar"
 * is present in both the child and parent tables in a TABLE_PER_SUBCLASS
 * hierarchy. See the TODO on the class TblPerSubClass below for the cause and
 * fix.
 */

public class FromBuilder
{
	/**
	 * join graph;
	 */
    private IJoinGraph joinGraph;

    /**
     * root.
     */
    private IExpression root;

    /**
     * length of the alias name.
     */
    private static final int ALIAS_NAME_LENGTH = 25;

    /**
     * from clause.
     */
    private final String fromClause;

    /**
     * Parameterized constructor.
     * @param joinGraph joinGraph
     */
    public FromBuilder(IJoinGraph joinGraph)
    {
        if (joinGraph == null)
        {
            this.fromClause = "";
            return;
        }
        this.joinGraph = joinGraph;
        try
        {
            root = joinGraph.getRoot();
        }
        catch (MultipleRootsException e)
        {
            throw new IllegalArgumentException(e);
        }
        this.fromClause = buildFrom();
    }

    /**
     * To get the Alias Name for the given IExpression. It will return alias
     * name for the DE entity associated with constraint entity.
     * @param expr expression
     * @return The Alias Name for the given Entity.
     */
    String aliasOf(IExpression expr)
    {
        String entName = entity(expr).getName();
        String className = entName.substring(entName.lastIndexOf('.') + 1, entName.length());
        return alias(className, expr);
    }

    /**
     * Get the alias of the string passed.
     * @param string string
     * @param expr expression
     * @return alias
     */
    private String alias(String string, IExpression expr)
    {
        string = Utility.removeSpecialCharactersFromString(string);
        if (string.length() > ALIAS_NAME_LENGTH)
        {
            string = string.substring(0, ALIAS_NAME_LENGTH);
        }
        return string + "_" + expr.getExpressionId();
    }

    /**
     * Return the alias of the expression.
     * @param attr attribute
     * @param expr expression
     * @return alias
     */
    protected String aliasOf(AttributeInterface attr, IExpression expr)
    {
        return aliasOf(expr) + "." + columnName(origAttr(attr));
    }

    /**
     * Returns the generated from clause.
     * @return from clause
     */
    protected String fromClause()
    {
        return fromClause;
    }

    /**
     * Builds the from clause.
     * @return from clause
     */
    private String buildFrom()
    {
        StringBuilder res = new StringBuilder();
        res.append(FROM);
        res.append(getExprSrc(root));

        Set<IExpression> currExprs = new HashSet<IExpression>();
        currExprs.addAll(children(root));

        while (!currExprs.isEmpty())
        {
            Set<IExpression> nextExprs = new HashSet<IExpression>();
            for (IExpression currExpr : currExprs)
            {
                nextExprs.addAll(children(currExpr));

                String src = getExprSrc(currExpr);
                String joinConds = getJoinConds(currExpr);
                res.append(oneExprStr(src, joinConds));
            }
            currExprs = nextExprs;
        }
        return res.toString();
    }

    /**
     * Add join conditions.
     * @param src source
     * @param joinConds join condition
     * @return string with join condition
     */
    private String oneExprStr(String src, String joinConds)
    {
        return LEFT_JOIN + src + ON + "(" + joinConds + ")";
    }

    /**
     * Get the join conditions.
     * @param expr expression
     * @return string
     */
    private String getJoinConds(IExpression expr)
    {
        StringBuilder res = new StringBuilder();
        String and = " and ";
        for (IExpression parent : parents(expr))
        {
            res.append(joinCond(parent, expr));
            res.append(and);
        }

        return removeLastOccur(res.toString(), and);
    }

    /**
     * @param leftExpr left expression
     * @param rightExpr right expression
     * @return join condition
     */
    private String joinCond(IExpression leftExpr, IExpression rightExpr)
    {
        AssociationInterface assoc = getAssociation(leftExpr, rightExpr);
        EntityInterface src = assoc.getEntity();
        EntityInterface tgt = assoc.getTargetEntity();

        ConstraintPropertiesInterface assocProps = assoc.getConstraintProperties();
        String leftAttr= assocProps.getSrcEntityConstraintKeyProperties() == null ?
        		null :assocProps.getSrcEntityConstraintKeyProperties().
        		getTgtForiegnKeyColumnProperties().getName();
       // String leftAttr = assocProps.getSrcEntityConstraintKeyProperties().
        //getTgtForiegnKeyColumnProperties().getName();
       // String rightAttr = assocProps.getTgtEntityConstraintKeyProperties().
        //getTgtForiegnKeyColumnProperties().getName();
        String rightAttr= assocProps.getTgtEntityConstraintKeyProperties() == null ?
        		null :assocProps.getTgtEntityConstraintKeyProperties().
        		getTgtForiegnKeyColumnProperties().getName();
        if (leftAttr != null && rightAttr != null)
        {
            // tricky choice of PK.
            return equate(middleTabAlias(assoc, rightExpr), assocProps.
            getTgtEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().getName(),
            aliasOf(rightExpr),primaryKey(entity(rightExpr)));
        }
        if (leftAttr == null)
        {
            leftAttr = primaryKey(src);
        }
        if (rightAttr == null)
        {
            rightAttr = primaryKey(tgt);
        }
        return equate(aliasOf(leftExpr), leftAttr, aliasOf(rightExpr), rightAttr);
    }

    /**
     * @param leftTab left Table
     * @param leftCol left Column
     * @param rightTab right Table
     * @param rightCol right Column
     * @return complete string
     */
    private String equate(String leftTab, String leftCol, String rightTab, String rightCol)
    {
        return leftTab + "." + leftCol + "=" + rightTab + "." + rightCol;
    }

    /**
     * Get association between left and right association.
     * @param leftExpr left expression
     * @param rightExpr right expression
     * @return association
     */
    private AssociationInterface getAssociation(IExpression leftExpr, IExpression rightExpr)
    {
        IIntraModelAssociation iAssociation = (IIntraModelAssociation)
        joinGraph.getAssociation(leftExpr, rightExpr);
        AssociationInterface assoc = iAssociation.getDynamicExtensionsAssociation();
        return InheritanceUtils.getInstance().getActualAassociation(assoc);
    }

    /**
     * Get list of children.
     * @param expr expression
     * @return list of children
     */
    private List<IExpression> children(IExpression expr)
    {
        return joinGraph.getChildrenList(expr);
    }

    /**
     * Get the list of parent expressions.
     * @param expr expression
     * @return list of parent expressions
     */
    private List<IExpression> parents(IExpression expr)
    {
        return joinGraph.getParentList(expr);
    }

    /**
     * Returns the alias.
     * @param assoc association
     * @param rightExpr right expression
     * @return alias
     */
    private String middleTabAlias(AssociationInterface assoc, IExpression rightExpr)
    {
        return alias(middleTabName(assoc), rightExpr);
    }

    /**
     * @param expr expression
     * @return string with join condition
     */
    private String getExprSrc(IExpression expr)
    {
        EntityInterface entity = entity(expr);

        SrcStringProvider srcStringProvider;
        if (isDerived(entity))
        {
            srcStringProvider = srcStringProvider(inheritanceStrategy(entity));
        }
        else
        {
            srcStringProvider = new DefaultSrcProvider();
        }
        String src = srcStringProvider.srcString(expr) + " " + aliasOf(expr);
        if (expr == root)
        {
            return src;
        }
        // many-many ??
        String res = "";
        for (IExpression parent : parents(expr))
        {
            AssociationInterface assoc = getAssociation(parent, expr);
            if (manyToMany(assoc))
            {
                ConstraintPropertiesInterface constraintProperty = assoc.getConstraintProperties();
                String middleTabAlias = middleTabAlias(assoc, expr);
                String joinCond = equate(aliasOf(parent), primaryKey(entity(parent)), middleTabAlias, constraintProperty
                        .getSrcEntityConstraintKeyProperties().getTgtForiegnKeyColumnProperties().getName());

                res += constraintProperty.getName() + " " + middleTabAlias + ON + joinCond;
            }
        }
        if ("".equals(res))
        {
            return src;
        }
        return res + LEFT_JOIN + src;
    }

    /**
     * Checks if the association is many to many.
     * @param assoc association
     * @return <CODE>true</CODE> if association is many to many,
	 * <CODE>false</CODE> otherwise
     */
    private boolean manyToMany(AssociationInterface assoc)
    {
        ConstraintPropertiesInterface constraintProperty = assoc.getConstraintProperties();
        return constraintProperty.getSrcEntityConstraintKeyProperties() != null && constraintProperty.getTgtEntityConstraintKeyProperties() != null;
    }

    /**
     * @param strategy inheritance strategy
     * @return the class according to inheritance strategy
     */
    private SrcStringProvider srcStringProvider(InheritanceStrategy strategy)
    {
        switch (strategy)
        {
            case TABLE_PER_CONCRETE_CLASS :
                return new TblPerConcreteClass();
            case TABLE_PER_HEIRARCHY :
                return new TblPerHier();
            case TABLE_PER_SUB_CLASS :
                return new TblPerSubClass();
            default :
                throw new RuntimeException("Unknown inheritance strategy.");
        }
    }

    /**
     *
     */
    private interface SrcStringProvider
    {
        String srcString(IExpression expression);
    }

    private static class DefaultSrcProvider implements SrcStringProvider
    {

        public String srcString(IExpression expression)
        {
            EntityInterface entity = entity(expression);
            AttributeInterface actAttr = activityStatus(entity);
            if (actAttr == null)
            {
                return tableName(entity);
            }
            return "(" + SELECT + "*" + FROM + tableName(entity) + WHERE + activeCond(columnName(actAttr)) + ")";
        }

    }

    private static class TblPerConcreteClass extends DefaultSrcProvider
    {
    }

    private static class TblPerHier implements SrcStringProvider
    {

        public String srcString(IExpression expression)
        {
            EntityInterface entity = entity(expression);

            String table = tableName(root(entity));
            return "(" + SELECT + "*" + FROM + table + WHERE + conds(entity) + ")";
        }

        private String conds(EntityInterface entity)
        {
            String res = discriminator(entity);
            AttributeInterface actAttr = activityStatus(entity);
            if (actAttr == null)
            {
                return res;
            }
            res += " and " + activeCond(columnName(actAttr));
            return res;
        }

        private static String discriminator(EntityInterface entity)
        {
            String columnName = entity.getDiscriminatorColumn();
            String columnValue = entity.getDiscriminatorValue();
            // Assuming Discriminator is of type String.
            String condition = columnName + "='" + columnValue + "'";
            return condition;
        }
    }

    private class TblPerSubClass implements SrcStringProvider
    {

        /**
         * TODO assumes a rosy practical database design where (1). SAME KEY for
         * whole hierarchy. this is theoretically FLAWED; a subclass may have a
         * different name for the key column than the superclass. But this isn't
         * supported by DE. (2). No clashes of column names across attributes in
         * the hierarchy; this makes the code easy. If/when a bug occurs when a
         * parent and child tables have same column name (for different
         * attributes), this is the place to fix the bug. FIX: generate unique
         * aliases for the columns in this SELECT clause.
         */
        public String srcString(IExpression expression)
        {
            EntityInterface child = entity(expression);
            return "(" + selectClause(expression) + fromClause(child) + whereClause(child) + ")";
        }

        /**
         * Form select clause.
         * @param expr expression
         * @return select clause
         */
        private String selectClause(IExpression expr)
        {
            EntityInterface entity = entity(expr);
            StringBuilder res = new StringBuilder();
            res.append(SELECT);

            final String comma = ", ";
            for (AttributeInterface attr : attributes(entity))
            {
                res.append(qualifiedColName(attr));
                res.append(comma);
            }
            for (IExpression parent : parents(expr))
            {
                AssociationInterface assoc = getAssociation(parent, expr);
              //  String fk = assoc.getConstraintProperties().getTgtEntityConstraintKeyProperties()
                //.getTgtForiegnKeyColumnProperties().getName();
                ConstraintKeyPropertiesInterface constraintProp = assoc.getConstraintProperties()
                .getTgtEntityConstraintKeyProperties();
                String foreignKey = constraintProp == null ? null : constraintProp.getTgtForiegnKeyColumnProperties().getName();
                if (foreignKey != null && !manyToMany(assoc))
                {
                    foreignKey = tableName(assoc.getTargetEntity()) + "." + foreignKey;
                    res.append(foreignKey);
                    res.append(comma);
                }
            }
            for (IExpression child : children(expr))
            {
                AssociationInterface assoc = getAssociation(expr, child);
                ConstraintKeyPropertiesInterface constraintProp = assoc.getConstraintProperties()
                .getSrcEntityConstraintKeyProperties();
                String foreignKey = constraintProp == null ? null : constraintProp.getTgtForiegnKeyColumnProperties().getName();
                if (foreignKey != null && !manyToMany(assoc))
                {
                    foreignKey = tableName(assoc.getEntity()) + "." + foreignKey;
                    res.append(foreignKey);
                    res.append(comma);
                }
            }
            return removeLastOccur(res.toString(), comma);
        }

        /**
         * Return from clause.
         * @param child entity
         * @return from clause
         */
        private String fromClause(EntityInterface child)
        {
            StringBuilder res = new StringBuilder();
            res.append(FROM);
            res.append(tableName(child));
            EntityInterface parent = child.getParentEntity();
            while (parent != null)
            {
                res.append(INNER_JOIN);
                res.append(tableName(parent));
                res.append(" on ");
                res.append(joinWithParent(child));

                child = parent;
                parent = parent.getParentEntity();
            }

            return res.toString();
        }

        /**
         * Join condition.
         * @param entity entity
         * @return join condition
         */
        private String joinWithParent(EntityInterface entity)
        {
            return key(entity) + "=" + key(entity.getParentEntity());
        }

        /**
         * @param entity entity
         * @return primary key
         */
        private String key(EntityInterface entity)
        {
            return tableName(entity) + "." + primaryKey(entity);
        }

        /**
         * @param attr attribute
         * @return Qualified column name
         */
        private String qualifiedColName(AttributeInterface attr)
        {
            return tableName(origAttr(attr).getEntity()) + "." + columnName(attr);
        }

        /**
         * Return the where clause with activity status condition.
         * @param child child
         * @return where clause
         */
        private String whereClause(EntityInterface child)
        {
            AttributeInterface actAttr = activityStatus(child);
            if (actAttr == null)
            {
                return "";
            }
            return WHERE + activeCond(qualifiedColName(actAttr));
        }
    }

    // entity properties
    /**
     * @param entity entity
     * @return parent entity
     */
    private static EntityInterface root(EntityInterface entity)
    {
        // old code also checked inheritance strategy here; not needed if
        // strategies are not mixed.
        while (entity.getParentEntity() != null)
        {
            entity = entity.getParentEntity();
        }
        return entity;
    }

    /**
     * Returns the entity.
     * @param expr expression
     * @return entity
     */
    private static EntityInterface entity(IExpression expr)
    {
        return expr.getQueryEntity().getDynamicExtensionsEntity();
    }

    /**
     * Returns the collection of attributes of the passed entity.
     * @param entity entity
     * @return attributes
     */
    private static Collection<AttributeInterface> attributes(EntityInterface entity)
    {
        return entity.getEntityAttributesForQuery();
    }

    /**
     * Returns the inheritance strategy of the entity.
     * @param entity entity
     * @return inheritance strategy
     */
    private static InheritanceStrategy inheritanceStrategy(EntityInterface entity)
    {
        return entity.getInheritanceStrategy();
    }

    /**
     * Checks if the entity is a child of any other entity.
     * @param entity entity
     * @return <CODE>true</CODE> passed entity is child of some other entity,
	 * <CODE>false</CODE> otherwise
     */
    private static boolean isDerived(EntityInterface entity)
    {
        return entity.getParentEntity() != null;
    }

    /**
     * Returns the table name.
     * @param entity entity
     * @return table name
     */
    private static String tableName(EntityInterface entity)
    {
        return entity.getTableProperties().getName();
    }

    /**
     * Gets the column name.
     * @param attr attribute
     * @return column name
     */
    private static String columnName(AttributeInterface attr)
    {
        return origAttr(attr).getColumnProperties().getName();
    }

    /**
     * Returns the middle table name.
     * @param assoc association
     * @return middle table name
     */
    private static String middleTabName(AssociationInterface assoc)
    {
        return assoc.getConstraintProperties().getName();
    }

    /**
     * Returns the actual attribute.
     * @param attr attribute
     * @return attribute
     */
    private static AttributeInterface origAttr(AttributeInterface attr)
    {
        return InheritanceUtils.getInstance().getActualAttribute(attr);
    }

    /**
     * To get the primary key attribute of the given entity.
     * @param entity the DE entity.
     * @return The Primary key attribute of the given entity.
     */
    private static String primaryKey(EntityInterface entity)
    {
        Collection<AttributeInterface> attributes = attributes(entity);
        for (AttributeInterface attribute : attributes)
        {
            if (attribute.getIsPrimaryKey() || attribute.getName().equals("id"))
            {
                return columnName(attribute);
            }
        }
        EntityInterface parentEntity = entity.getParentEntity();
        if (parentEntity != null)
        {
            return primaryKey(parentEntity);
        }
        throw new RuntimeException("No Primary key attribute found for Entity:" + entity.getName());
    }

    /**
     * Check for activity status present in entity.
     * @param entity The Entity for which we required to check if
     *            activity status present.
     * @return Reference to the AttributeInterface if activityStatus attribute
     *         present in the entity, else null.
     */
    private static AttributeInterface activityStatus(EntityInterface entity)
    {
        for (AttributeInterface attribute : attributes(entity))
        {
            if (attribute.getName().equals(edu.wustl.query.util.global.AQConstants.ACTIVITY_STATUS))
            {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Adds activity status condition.
     * @param attr attribute
     * @return attribute
     */
    private static String activeCond(String attr)
    {
        return attr + " != '" + edu.wustl.query.util.global.AQConstants.ACTIVITY_STATUS_DISABLED + "'";
    }

    /**
     * Removes the last occurrence of string specified by 'toRemove' from the source.
     * @param src source
     * @param toRemove toRemove
     * @return the source
     */
    private static String removeLastOccur(String src, String toRemove)
    {
        if (src.endsWith(toRemove))
        {
            src = src.substring(0, src.length() - toRemove.length());
        }
        return src;
    }
}