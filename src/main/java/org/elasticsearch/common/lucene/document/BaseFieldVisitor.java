begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.common.lucene.document
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|StoredFieldVisitor
import|;
end_import

begin_class
DECL|class|BaseFieldVisitor
specifier|public
specifier|abstract
class|class
name|BaseFieldVisitor
extends|extends
name|StoredFieldVisitor
block|{
comment|// LUCENE 4 UPGRADE: Added for now to make everything work. Want to make use of Document as less as possible.
DECL|method|createDocument
specifier|public
specifier|abstract
name|Document
name|createDocument
parameter_list|()
function_decl|;
comment|// LUCENE 4 UPGRADE: Added for now for compatibility with Selectors
DECL|method|reset
specifier|public
specifier|abstract
name|void
name|reset
parameter_list|()
function_decl|;
block|}
end_class

end_unit

