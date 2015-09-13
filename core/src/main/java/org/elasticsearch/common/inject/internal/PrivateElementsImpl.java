begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2008 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject.internal
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|Binder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|Injector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|Key
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|PrivateBinder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|spi
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|spi
operator|.
name|ElementVisitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
operator|.
name|spi
operator|.
name|PrivateElements
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|Preconditions
operator|.
name|checkArgument
import|;
end_import

begin_comment
comment|/**  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|PrivateElementsImpl
specifier|public
specifier|final
class|class
name|PrivateElementsImpl
implements|implements
name|PrivateElements
block|{
comment|/*     * This class acts as both a value object and as a builder. When getElements() is called, an     * immutable collection of elements is constructed and the original mutable list is nulled out.     * Similarly, the exposed keys are made immutable on access.     */
DECL|field|source
specifier|private
specifier|final
name|Object
name|source
decl_stmt|;
DECL|field|elementsMutable
specifier|private
name|List
argument_list|<
name|Element
argument_list|>
name|elementsMutable
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|exposureBuilders
specifier|private
name|List
argument_list|<
name|ExposureBuilder
argument_list|<
name|?
argument_list|>
argument_list|>
name|exposureBuilders
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**      * lazily instantiated      */
DECL|field|elements
specifier|private
name|List
argument_list|<
name|Element
argument_list|>
name|elements
decl_stmt|;
comment|/**      * lazily instantiated      */
DECL|field|exposedKeysToSources
specifier|private
name|ImmutableMap
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|,
name|Object
argument_list|>
name|exposedKeysToSources
decl_stmt|;
DECL|field|injector
specifier|private
name|Injector
name|injector
decl_stmt|;
DECL|method|PrivateElementsImpl
specifier|public
name|PrivateElementsImpl
parameter_list|(
name|Object
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|source
argument_list|,
literal|"source"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|Object
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
annotation|@
name|Override
DECL|method|getElements
specifier|public
name|List
argument_list|<
name|Element
argument_list|>
name|getElements
parameter_list|()
block|{
if|if
condition|(
name|elements
operator|==
literal|null
condition|)
block|{
name|elements
operator|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|elementsMutable
argument_list|)
expr_stmt|;
name|elementsMutable
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|elements
return|;
block|}
annotation|@
name|Override
DECL|method|getInjector
specifier|public
name|Injector
name|getInjector
parameter_list|()
block|{
return|return
name|injector
return|;
block|}
DECL|method|initInjector
specifier|public
name|void
name|initInjector
parameter_list|(
name|Injector
name|injector
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|injector
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"injector already initialized"
argument_list|)
throw|;
block|}
name|this
operator|.
name|injector
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|injector
argument_list|,
literal|"injector"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExposedKeys
specifier|public
name|Set
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|>
name|getExposedKeys
parameter_list|()
block|{
if|if
condition|(
name|exposedKeysToSources
operator|==
literal|null
condition|)
block|{
name|Map
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|,
name|Object
argument_list|>
name|exposedKeysToSourcesMutable
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ExposureBuilder
argument_list|<
name|?
argument_list|>
name|exposureBuilder
range|:
name|exposureBuilders
control|)
block|{
name|exposedKeysToSourcesMutable
operator|.
name|put
argument_list|(
name|exposureBuilder
operator|.
name|getKey
argument_list|()
argument_list|,
name|exposureBuilder
operator|.
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|exposedKeysToSources
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|exposedKeysToSourcesMutable
argument_list|)
expr_stmt|;
name|exposureBuilders
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|exposedKeysToSources
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|acceptVisitor
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|acceptVisitor
parameter_list|(
name|ElementVisitor
argument_list|<
name|T
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|getElementsMutable
specifier|public
name|List
argument_list|<
name|Element
argument_list|>
name|getElementsMutable
parameter_list|()
block|{
return|return
name|elementsMutable
return|;
block|}
DECL|method|addExposureBuilder
specifier|public
name|void
name|addExposureBuilder
parameter_list|(
name|ExposureBuilder
argument_list|<
name|?
argument_list|>
name|exposureBuilder
parameter_list|)
block|{
name|exposureBuilders
operator|.
name|add
argument_list|(
name|exposureBuilder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|applyTo
specifier|public
name|void
name|applyTo
parameter_list|(
name|Binder
name|binder
parameter_list|)
block|{
name|PrivateBinder
name|privateBinder
init|=
name|binder
operator|.
name|withSource
argument_list|(
name|source
argument_list|)
operator|.
name|newPrivateBinder
argument_list|()
decl_stmt|;
for|for
control|(
name|Element
name|element
range|:
name|getElements
argument_list|()
control|)
block|{
name|element
operator|.
name|applyTo
argument_list|(
name|privateBinder
argument_list|)
expr_stmt|;
block|}
name|getExposedKeys
argument_list|()
expr_stmt|;
comment|// ensure exposedKeysToSources is populated
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Key
argument_list|<
name|?
argument_list|>
argument_list|,
name|Object
argument_list|>
name|entry
range|:
name|exposedKeysToSources
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|privateBinder
operator|.
name|withSource
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|expose
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getExposedSource
specifier|public
name|Object
name|getExposedSource
parameter_list|(
name|Key
argument_list|<
name|?
argument_list|>
name|key
parameter_list|)
block|{
name|getExposedKeys
argument_list|()
expr_stmt|;
comment|// ensure exposedKeysToSources is populated
name|Object
name|source
init|=
name|exposedKeysToSources
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|checkArgument
argument_list|(
name|source
operator|!=
literal|null
argument_list|,
literal|"%s not exposed by %s."
argument_list|,
name|key
argument_list|,
name|this
argument_list|)
expr_stmt|;
return|return
name|source
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|ToStringBuilder
argument_list|(
name|PrivateElements
operator|.
name|class
argument_list|)
operator|.
name|add
argument_list|(
literal|"exposedKeys"
argument_list|,
name|getExposedKeys
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
literal|"source"
argument_list|,
name|getSource
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

