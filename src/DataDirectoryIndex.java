//!
//! @title          Binary Sanctity
//! @author         Jonathan Smith
//! Course Section: CMIS201-HYB2 (Seidel)
//! @file           DataDirectoryIndex.java
//! @description    Enum describing the types of data directories found within
//!                 a portable executable file. The ordinal maps directly to the
//!                 data directory's index.
//!

public enum DataDirectoryIndex
{
  EXPORT_TABLE,
  IMPORT_TABLE,
  RESOURCE_TABLE,
  EXCEPTION_TABLE,
  CERTIFICATE_TABLE,
  BASE_RELOCATION_TABLE,
  DEBUG,
  ARCHITECTURE,
  GLOBAL_POINTER,
  TLS_TABLE,
  LOAD_CONFIG_TABLE,
  BOUND_IMPORT,
  IMPORT_ADDRESS_TABLE,
  DELAY_IMPORT_DESCRIPTOR,
  CLR_RUNTIME_HEADER,
  RESERVED
}
