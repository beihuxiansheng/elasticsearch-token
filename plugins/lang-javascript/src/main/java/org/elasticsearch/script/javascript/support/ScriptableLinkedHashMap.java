begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.script.javascript.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|script
operator|.
name|javascript
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|mozilla
operator|.
name|javascript
operator|.
name|Scriptable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Implementation of a Scriptable Map. This is the best choice for maps that want to represent  * JavaScript associative arrays - allowing access via key and integer index. It maintains and  * respects insertion order of the elements and allows either string or integer keys.  *  *  */
end_comment

begin_class
DECL|class|ScriptableLinkedHashMap
specifier|public
class|class
name|ScriptableLinkedHashMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|LinkedHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
implements|implements
name|ScriptableMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|3774167893214964123L
decl_stmt|;
DECL|field|parentScope
specifier|private
name|Scriptable
name|parentScope
decl_stmt|;
DECL|field|prototype
specifier|private
name|Scriptable
name|prototype
decl_stmt|;
DECL|method|ScriptableLinkedHashMap
specifier|public
name|ScriptableLinkedHashMap
parameter_list|()
block|{     }
DECL|method|ScriptableLinkedHashMap
specifier|public
name|ScriptableLinkedHashMap
parameter_list|(
name|int
name|initialCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|)
expr_stmt|;
block|}
DECL|method|ScriptableLinkedHashMap
specifier|public
name|ScriptableLinkedHashMap
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|source
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#getClassName()      */
DECL|method|getClassName
specifier|public
name|String
name|getClassName
parameter_list|()
block|{
return|return
literal|"ScriptableMap"
return|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#get(java.lang.String, org.mozilla.javascript.Scriptable)      */
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|,
name|Scriptable
name|start
parameter_list|)
block|{
comment|// get the property from the underlying QName map
if|if
condition|(
literal|"length"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|size
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#get(int, org.mozilla.javascript.Scriptable)      */
DECL|method|get
specifier|public
name|Object
name|get
parameter_list|(
name|int
name|index
parameter_list|,
name|Scriptable
name|start
parameter_list|)
block|{
name|Object
name|value
init|=
literal|null
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|Iterator
name|itrValues
init|=
name|this
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|++
operator|<=
name|index
operator|&&
name|itrValues
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|value
operator|=
name|itrValues
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|value
return|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#has(java.lang.String, org.mozilla.javascript.Scriptable)      */
DECL|method|has
specifier|public
name|boolean
name|has
parameter_list|(
name|String
name|name
parameter_list|,
name|Scriptable
name|start
parameter_list|)
block|{
comment|// locate the property in the underlying map
return|return
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#has(int, org.mozilla.javascript.Scriptable)      */
DECL|method|has
specifier|public
name|boolean
name|has
parameter_list|(
name|int
name|index
parameter_list|,
name|Scriptable
name|start
parameter_list|)
block|{
return|return
operator|(
name|index
operator|>=
literal|0
operator|&&
name|this
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
operator|>
name|index
operator|)
return|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#put(java.lang.String, org.mozilla.javascript.Scriptable, java.lang.Object)      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|put
specifier|public
name|void
name|put
parameter_list|(
name|String
name|name
parameter_list|,
name|Scriptable
name|start
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// add the property to the underlying QName map
name|put
argument_list|(
operator|(
name|K
operator|)
name|name
argument_list|,
operator|(
name|V
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#put(int, org.mozilla.javascript.Scriptable, java.lang.Object)      */
DECL|method|put
specifier|public
name|void
name|put
parameter_list|(
name|int
name|index
parameter_list|,
name|Scriptable
name|start
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// TODO: implement?
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#delete(java.lang.String)      */
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// remove the property from the underlying QName map
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#delete(int)      */
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|Iterator
name|itrKeys
init|=
name|this
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|<=
name|index
operator|&&
name|itrKeys
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Object
name|key
init|=
name|itrKeys
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|index
condition|)
block|{
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#getPrototype()      */
DECL|method|getPrototype
specifier|public
name|Scriptable
name|getPrototype
parameter_list|()
block|{
return|return
name|this
operator|.
name|prototype
return|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#setPrototype(org.mozilla.javascript.Scriptable)      */
DECL|method|setPrototype
specifier|public
name|void
name|setPrototype
parameter_list|(
name|Scriptable
name|prototype
parameter_list|)
block|{
name|this
operator|.
name|prototype
operator|=
name|prototype
expr_stmt|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#getParentScope()      */
DECL|method|getParentScope
specifier|public
name|Scriptable
name|getParentScope
parameter_list|()
block|{
return|return
name|this
operator|.
name|parentScope
return|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#setParentScope(org.mozilla.javascript.Scriptable)      */
DECL|method|setParentScope
specifier|public
name|void
name|setParentScope
parameter_list|(
name|Scriptable
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parentScope
operator|=
name|parent
expr_stmt|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#getIds()      */
DECL|method|getIds
specifier|public
name|Object
index|[]
name|getIds
parameter_list|()
block|{
return|return
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|()
return|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#getDefaultValue(java.lang.Class)      */
DECL|method|getDefaultValue
specifier|public
name|Object
name|getDefaultValue
parameter_list|(
name|Class
name|hint
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @see org.mozilla.javascript.Scriptable#hasInstance(org.mozilla.javascript.Scriptable)      */
DECL|method|hasInstance
specifier|public
name|boolean
name|hasInstance
parameter_list|(
name|Scriptable
name|instance
parameter_list|)
block|{
return|return
name|instance
operator|instanceof
name|ScriptableLinkedHashMap
return|;
block|}
block|}
end_class

end_unit

