def find_true_duplicates(file_content):
    # Split the content into lines
    lines = file_content.strip().split('\n')
    
    # Initialize a dictionary to store blobs and their line numbers
    blob_dict = {}
    duplicates = []
    
    # Process each line
    for line_num, line in enumerate(lines, 1):
        # Skip comments and empty lines
        if line.startswith('#') or not line.strip():
            continue
            
        # Extract the blob path (removing any suffix after semicolon)
        blob_path = line.split(';')[0].strip()
        
        # For files being renamed, extract the source path
        if ':' in blob_path:
            blob_path = blob_path.split(':')[0].strip()
        
        # Store the blob path and its line number
        if blob_path in blob_dict:
            duplicates.append((blob_path, blob_dict[blob_path], line_num))
        else:
            blob_dict[blob_path] = line_num
            
    return duplicates

def get_line_content(file_content, line_num):
    lines = file_content.strip().split('\n')
    if 1 <= line_num <= len(lines):
        return lines[line_num - 1]
    return "Line not found"

def main():
    # Read the file content from the input
    with open('proprietary-files.txt', 'r') as f:
        file_content = f.read()

    # Find true duplicates
    duplicates = find_true_duplicates(file_content)

    # Print results
    if duplicates:
        print(f"Found {len(duplicates)} true duplicates (exact same path):")
        for blob_path, first_line, second_line in duplicates:
            first_line_content = get_line_content(file_content, first_line)
            second_line_content = get_line_content(file_content, second_line)
            print(f"Duplicate blob: {blob_path}")
            print(f"  Line {first_line}: {first_line_content}")
            print(f"  Line {second_line}: {second_line_content}")
            print("")
    else:
        print("No true duplicates found with the exact same path.")

if __name__ == "__main__":
    main()