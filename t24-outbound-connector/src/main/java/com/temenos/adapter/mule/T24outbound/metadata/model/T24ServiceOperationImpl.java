package com.temenos.adapter.mule.T24outbound.metadata.model;

import com.temenos.adapter.common.metadata.ServiceOperation;
import com.temenos.adapter.common.metadata.outbound.ActionType;

public class T24ServiceOperationImpl implements ServiceOperation
{
	private String name;
	private String target;
	private String action;
	
	public T24ServiceOperationImpl(String name, String target, String action)
	{
		  if (name==null || name.isEmpty()) {
		    throw new IllegalArgumentException("Invalid service operation name [" + name + "]");
		  }
		  if (name==null || name.isEmpty()) {
		    throw new IllegalArgumentException("Invalid target name [" + target + "]");
		  }
		  if (!ActionType.isValidActionString(action)) {
		    throw new IllegalArgumentException("Invalid action name [" + action + "]");
		  }
		  this.name = name;
		  this.target = target;
		  this.action = action;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String getTarget()
	{
		return this.target;
	}
	
	public String getAction()
	{
		return this.action;
	}
	
	@Deprecated
	public void setName(String name)
	{
		this.name = name;
	}
	
	@Deprecated
	public void setTarget(String target)
	{
		this.target = target;
	}
	
	@Deprecated
	public void setAction(String action)
	{
		this.action = action;
	}
	
	public ActionType getActionType()
	{
		return ActionType.fromString(this.action);
	}
	
	public String toString()
	{
		return "name=" + this.name + ", target=" + this.target + ", action=" + this.action;
	}
}