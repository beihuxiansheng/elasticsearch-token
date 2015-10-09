begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2010 Google Inc.  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
import|;
end_import

begin_comment
comment|/**  * Whether a member supports null values injected.  *<p>Support for {@code Nullable} annotations in Guice is loose.  * Any annotation type whose simplename is "Nullable" is sufficient to indicate  * support for null values injected.  *<p>This allows support for JSR-305's  *<a href="http://groups.google.com/group/jsr-305/web/proposed-annotations">  * javax.annotation.meta.Nullable</a> annotation and IntelliJ IDEA's  *<a href="http://www.jetbrains.com/idea/documentation/howto.html">  * org.jetbrains.annotations.Nullable</a>.  *  * @author jessewilson@google.com (Jesse Wilson)  */
end_comment

begin_class
DECL|class|Nullability
specifier|public
class|class
name|Nullability
block|{
DECL|method|Nullability
specifier|private
name|Nullability
parameter_list|()
block|{     }
DECL|method|allowsNull
specifier|public
specifier|static
name|boolean
name|allowsNull
parameter_list|(
name|Annotation
index|[]
name|annotations
parameter_list|)
block|{
for|for
control|(
name|Annotation
name|a
range|:
name|annotations
control|)
block|{
if|if
condition|(
literal|"Nullable"
operator|.
name|equals
argument_list|(
name|a
operator|.
name|annotationType
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

