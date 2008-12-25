package edu.wustl.common.query.pvmanager;

import java.util.List;

import edu.common.dynamicextensions.domaininterface.AttributeInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.PermissibleValueInterface;
import edu.wustl.common.query.pvmanager.impl.PVManagerException;

public interface IPermissibleValueManager
{
	public List<PermissibleValueInterface> getPermissibleValueList(AttributeInterface attribute,EntityInterface entity) throws PVManagerException;
	public boolean showListBoxForPV(AttributeInterface attribute,EntityInterface entity) throws PVManagerException;
	public boolean isEnumerated(AttributeInterface attribute,EntityInterface entity);
}
