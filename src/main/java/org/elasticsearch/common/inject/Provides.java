begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Copyright (C) 2007 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.inject
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|inject
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Documented
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Annotates methods of a {@link Module} to create a provider method binding. The method's return  * type is bound to it's returned value. Guice will pass dependencies to the method as parameters.  *  * @author crazybob@google.com (Bob Lee)  * @since 2.0  */
end_comment

begin_annotation_defn
annotation|@
name|Documented
annotation|@
name|Target
argument_list|(
name|METHOD
argument_list|)
annotation|@
name|Retention
argument_list|(
name|RUNTIME
argument_list|)
DECL|interface|Provides
specifier|public
annotation_defn|@interface
name|Provides
block|{ }
end_annotation_defn

end_unit

